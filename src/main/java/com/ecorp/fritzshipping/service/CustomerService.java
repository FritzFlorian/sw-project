package com.ecorp.fritzshipping.service;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Order;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.service.external.MailHelperIF;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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


@RequestScoped
@WebService(serviceName="CustomerService", portName="CustomerPort")
public class CustomerService implements CustomerServiceIF, Serializable {
    private static final String algorithm = "SHA-256";
    
    @PersistenceContext
    private EntityManager em;
    @Inject
    private DeliveryServiceIF deliveryService;
    @Inject
    private MailHelperIF mailHelper;
    
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
        try {
            mailHelper.sendRegistrationMail(newCustomer);
        } catch(WebServiceException e) {
            logger.info("Could not send registration/greeting mail to new customer. Proceeding as it is not crucial.");
        }
        
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
        if (foundCustomer.getPassword().equals(hashedPassword)){
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
    public Order placeOrder(Customer customer, List<Shipment> shipments) throws AuthenticationException, ShipmentException {
        Customer loadedCustomer = login(customer);
        if (loadedCustomer ==  null) {
            throw new AuthenticationException("Ivalid Customer email/password!");
        }
        
        int totalPrice = 0;
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
        
        // TODO: Call Bank IF
        long paymentTransactionID = 0;
        
        // Create and persist the order.
        Order newOrder = new Order(new Date(), paymentTransactionID, totalPrice,
                                   customer, storedShipments);
        em.persist(newOrder);
        // We also need to update the customers collection here!
        loadedCustomer.addOrder(newOrder);
        
        logger.info("New order placed ({}).", newOrder.getId());
        return newOrder;
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
