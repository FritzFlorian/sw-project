package com.ecorp.fritzshipping.service.external;

import com.ecorp.fritzshipping.entity.Customer;

/**
 * Helper to wrap boilerplate from the external mail webservice.
 */
public interface MailHelperIF {
    public void sendRegistrationMail(Customer customer);
}
