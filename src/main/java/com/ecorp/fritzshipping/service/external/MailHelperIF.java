package com.ecorp.fritzshipping.service.external;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingPoint;

/**
 * Helper to wrap boilerplate from the external mail webservice.
 */
public interface MailHelperIF {
    /**
     * Send a registration email to the given customer.
     * 
     * @param customer The customer to receive the registration mail. 
     */
    public void sendRegistrationMail(Customer customer);
    
    /**
     * Send an mail about the progress of the given shipment to
     * the given email address.
     * Note: It's not send out directly to all registered recipients,
     * as this service is only intended as a wrapper on the external
     * mail client.
     * 
     * @param shipment The shipment to send the progress mail.
     * @param trackingPoint The tracking point the notification is about.
     * @param email The email address to receive the shipment progress mail.
     */
    public void sendShipmentProgerssMail(Shipment shipment, TrackingPoint trackingPoint, String email);
}
