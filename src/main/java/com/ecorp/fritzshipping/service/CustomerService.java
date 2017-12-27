package com.ecorp.fritzshipping.service;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Order;
import com.ecorp.fritzshipping.entity.Shipment;
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
import org.apache.logging.log4j.Logger;


@RequestScoped
@WebService(serviceName="CustomerService", portName="CustomerPort")
public class CustomerService implements CustomerIF{

    @PersistenceContext
    private EntityManager em;
    @Inject
    private DeliveryIF deliveryService;
    
    @Inject
    private Logger logger;

    @Override
    @Transactional
    @WebMethod(exclude=true)
    public Customer createCustomer(Customer newCustomer) {
        em.persist(newCustomer);
        
        logger.info("New customer with email {} registered.", newCustomer.getEmail());
        return newCustomer;
    }

    @Override
    @Transactional
    @WebMethod(exclude=true)
    public Customer login(Customer unauthorizedCustomer) {
        TypedQuery<Customer> query = 
                em.createQuery("SELECT c "
                             + "FROM Customer c "
                             + "WHERE c.email=:email and "
                             + "      c.password=:password", Customer.class);
        query.setParameter("email", unauthorizedCustomer.getEmail());
        query.setParameter("password", unauthorizedCustomer.getPassword());
        
        List<Customer>foundCustomers = query.getResultList();
        if (foundCustomers.size() > 0) {
            logger.debug("Login Success.");
            return foundCustomers.get(0);
        } else {
            logger.debug("Login Failed.");
            return null;
        }
    }
    
    @Override
    @Transactional
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
    public Order placeOrder(Customer customer, List<Shipment> shipments) throws ShipmentException {
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
        Customer loadedCustomer = em.find(Customer.class, customer.getId());
        loadedCustomer.addOrder(newOrder);
        
        logger.info("New order placed ({}).", newOrder.getId());
        return newOrder;
    }

    @Override
    @Transactional
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
    
}
