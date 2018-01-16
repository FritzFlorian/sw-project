package com.ecorp.fritzshipping.service;

import com.ecorp.bank.service.AccountingServiceService;
import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Order;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.service.external.MailHelperIF;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.xml.ws.WebServiceException;
import org.apache.logging.log4j.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.ws.WebServiceRef;


@RequestScoped
@WebService(serviceName="CustomerService", portName="CustomerPort")
public class CustomerService implements CustomerServiceIF, Serializable {
    private static final long MY_BANK_ACCOUNT_ID = 16;
    private static final long MY_BANK_CUSTOMER_ID = 15;
    
    private static final String algorithm = "SHA-256";
    
    @PersistenceContext
    private EntityManager em;
    @Inject
    private DeliveryServiceIF deliveryService;
    
    @Inject
    private MailHelperIF mailHelper;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/im-lamport.othr.de_8080/ecorp-bank/AccountingService.wsdl")
    private AccountingServiceService accountingServiceRef;
    
    @Inject
    private Logger logger;
    
    @Override
    @Transactional
    @WebMethod(exclude=true)
    public Customer createCustomer(Customer newCustomer) {
        // Hash password, email should be fine as we nerver change it (it's the primary key/unique account name),
        // only purpose is to not have the same salt for every account
        newCustomer.setPassword(saltAndHash(newCustomer.getPassword(), newCustomer.getEmail()));
        
        em.persist(newCustomer);
        mailHelper.sendRegistrationMail(newCustomer);
        
        logger.info("New customer with email {} registered.", newCustomer.getEmail());
        return newCustomer;
    }

    @Override
    @WebMethod(exclude=true)
    public Customer login(Customer unauthorizedCustomer) {
        Customer foundCustomer = em.find(Customer.class, unauthorizedCustomer.getEmail());
        
        if (foundCustomer == null) {
            logger.debug("Login Failed.");
            return null;            
        } 
        
        String hashedPassword = saltAndHash(unauthorizedCustomer.getPassword(), unauthorizedCustomer.getEmail());
        // Also compare against 'plain' password,
        // as we only work with that one when we load a customer internally
        // and use that customer to call a service.
        if (foundCustomer.getPassword().equals(hashedPassword) 
                || foundCustomer.getPassword().equals(unauthorizedCustomer.getPassword())){
            logger.debug("Login Success.");
            return foundCustomer;
        }
        
        logger.debug("Login Failed.");
        return null;
    }
    
    @Override
    @WebMethod(exclude=true)
    public List<Order> getOrders(Customer customer) {
        TypedQuery<Order> query =
                em.createQuery("Select o "
                             + "FROM Order o "
                             + "JOIN o.customer "
                             + "WHERE o.customer.id=:customerId", Order.class);
        query.setParameter("customerId", customer.getId());
        
        return query.getResultList();
    }

    @Override
    @Transactional
    @WebMethod
    public Order placeOrder(Customer customer, List<Shipment> shipments) throws OrderException, ShipmentException {
        Customer loadedCustomer = login(customer);
        if (loadedCustomer ==  null) {
            throw new OrderException("Ivalid Customer email/password!");
        }
        
        long totalPrice = 0;
        List<Shipment> storedShipments = new LinkedList<>();
        for (Shipment shipment : shipments) {
            // Only use attributes that we want to be able to be set
            // from our caller.
            Shipment toCreate = new Shipment();
            toCreate.setPickup(shipment.isPickup());
            toCreate.setSender(shipment.getSender());
            toCreate.setRecipient(shipment.getRecipient());
            toCreate.setType(shipment.getType());
            toCreate.setWeight(shipment.getWeight());
            
            storedShipments.add(deliveryService.createShipment(toCreate));
            totalPrice += shipment.getPrice();
        }
        
        long paymentTransactionID = requestDebit(loadedCustomer.getBankAccountId(), totalPrice, "");
        
        // Create and persist the order.
        Order newOrder = new Order(new Date(), paymentTransactionID, totalPrice,
                                   customer, storedShipments);
        em.persist(newOrder);
        // We also need to update the customers collection here!
        loadedCustomer.addOrder(newOrder);
        
        logger.info("New order placed ({}).", newOrder.getId());
        return newOrder;
    }
    
    private long requestDebit(long accountId, long ammount, String reference) throws OrderException {
        com.ecorp.bank.service.Account from = new com.ecorp.bank.service.Account();
        from.setId(accountId);
        
        com.ecorp.bank.service.Account to = new com.ecorp.bank.service.Account();
        to.setId(MY_BANK_ACCOUNT_ID);
        
        com.ecorp.bank.service.Customer customer = new com.ecorp.bank.service.Customer();
        customer.setId(MY_BANK_CUSTOMER_ID);
        
        com.ecorp.bank.service.TransactionRequest transactionRequest = new com.ecorp.bank.service.TransactionRequest();
        transactionRequest.setFrom(from);
        transactionRequest.setTo(to);
        transactionRequest.setAmount(new BigDecimal(ammount).divide(new BigDecimal(100)));
        transactionRequest.setReference(reference);
        transactionRequest.setCustomer(customer);
        
        
        try { // Call Web Service Operation
            com.ecorp.bank.service.AccountingService port = accountingServiceRef.getAccountingServicePort();
            return port.requestDebit(transactionRequest).getId();
        } catch (com.ecorp.bank.service.TransactionException_Exception | WebServiceException e) {
            // Abort because of timeout in webservice or error in external service.
            // We do not want to continue here! We do not even consider creating
            // orders that are not payed for.
            throw new OrderException("Error with payment: " + e.getMessage());
        }
    }

    @Override
    @WebMethod(exclude=true)
    public Order getOrder(long id) {
        TypedQuery<Order> query = 
                em.createQuery("SELECT o "
                             + "FROM Order o "
                             + "LEFT JOIN FETCH o.shipments "
                             + "WHERE o.id=:id" , Order.class);
        query.setParameter("id", id);
        Order result = query.getSingleResult();
        
        return result;
    }
    
    public String saltAndHash(String password, String salt) {
        try {
            MessageDigest hashAlgo = MessageDigest.getInstance(algorithm);
            String toHash = salt + "#" + password;
            byte[] output = hashAlgo.digest(toHash.getBytes("UTF-8"));
            StringBuilder passwordBuilder = new StringBuilder();
            for(byte b : output)
                passwordBuilder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            return passwordBuilder.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException("Could not hash password", ex);
        }
    } 
}
