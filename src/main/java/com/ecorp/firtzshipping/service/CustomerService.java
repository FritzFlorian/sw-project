package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Customer;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;


@RequestScoped
public class CustomerService implements CustomerServiceIF{

    @PersistenceContext
    private EntityManager em;

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
                             + "FROM Customer AS c "
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
    
}
