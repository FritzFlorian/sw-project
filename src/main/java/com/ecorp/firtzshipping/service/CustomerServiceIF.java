package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Order;
import com.ecorp.fritzshipping.entity.Shipment;
import java.util.List;

public interface CustomerServiceIF {
    /**
     * @param newCustomer A pre filled customer to be created.
     * @return A new customer that is persistent in the database.
     */
    public Customer createCustomer(Customer newCustomer);
    
    /**
     * @param unauthorizedCustomer A customer with email and password filled in.
     * @return The authorized customer or null if the email/password did not match.
     */
    public Customer login(Customer unauthorizedCustomer);
    
    /**
     * Places an new order.
     * This will trigger the payment and add all shipments.
     * 
     * @param customer The customer that places the order.
     * @param shipments The shipments to be shipped with this order.
     * @throws ShipmentException Thrown if any constraint of an shipment was violated.
     * @return The correctly placed order.
     */
    public Order placeOrder(Customer customer, List<Shipment> shipments)
            throws ShipmentException;
    
    /**
     * Gets all orders of the given customer.
     * 
     * @param customer The customer to get the orders of.
     * @return All orders by that customer.
     */
    public List<Order> getOrders(Customer customer);
    
    /**
     * Loads an order by id.
     * Preloads all shipments.
     * 
     * @param id The id of the order to be loaded.
     * @return The loaded order.
     */
    public Order getOrder(long id);
}
