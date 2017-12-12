package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Customer;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class CustomerService implements CustomerServiceIF{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Customer createCustomer(Customer newCustomer) {
        em.persist(newCustomer);
        return newCustomer;
    }
    
}
