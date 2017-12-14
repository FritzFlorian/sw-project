package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Customer;

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
    
}
