package com.ecorp.fritzshipping.service.external;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import org.apache.logging.log4j.Logger;


@ApplicationScoped
@Alternative
public class MailHelperServiceMock implements MailHelperIF, Serializable {
    @Inject
    private Logger logger;

    @Override
    public void sendRegistrationMail(Customer customer) {
        logger.warn("Using Mocked Service to send registration email to {}.", customer.getEmail());
    }

    @Override
    public void sendShipmentProgerssMail(Shipment shipment, TrackingPoint trackingPoint, String email) {
        logger.warn("Using Mocked Service to send shipment update '{}' to '{}'. ({})", shipment.getId(), email, trackingPoint.getType());
    }
    
}
