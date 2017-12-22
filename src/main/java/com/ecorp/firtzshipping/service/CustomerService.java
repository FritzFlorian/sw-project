package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Order;
import com.ecorp.fritzshipping.entity.Shipment;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;


@RequestScoped
public class CustomerService implements CustomerServiceIF{

    @PersistenceContext
    private EntityManager em;
    @Inject
    private DeliveryIF deliveryService;

    @Override
    @Transactional
    public Customer createCustomer(Customer newCustomer) {
        em.persist(newCustomer);
        return newCustomer;
    }

    @Override
    @Transactional
    public Customer login(Customer unauthorizedCustomer) {
        TypedQuery<Customer> query = 
                em.createQuery("SELECT c "
                             + "FROM Customer c "
                             + "LEFT JOIN FETCH c.orders "
                             + "WHERE c.email=:email and "
                             + "      c.password=:password", Customer.class);
        query.setParameter("email", unauthorizedCustomer.getEmail());
        query.setParameter("password", unauthorizedCustomer.getPassword());
        
        List<Customer>foundCustomers = query.getResultList();
        if (foundCustomers.size() > 0) {
            return foundCustomers.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public Order placeOrder(Customer customer, List<Shipment> shipments) throws ShipmentException {
        int totalPrice = 0;
        List<Shipment> storedShipments = new LinkedList<>();
        for (Shipment shipment : shipments) {
            storedShipments.add(deliveryService.createShipment(shipment));
            totalPrice += shipment.getPrice();
        }
        
        // TODO: Call Bank IF
        long paymentTransactionID = 0;
        
        Order newOrder = new Order(new Date(), paymentTransactionID, totalPrice,
                                   customer, storedShipments);
        em.persist(newOrder);
        
        return newOrder;
    }

    @Override
    @Transactional
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
