package com.ecorp.fritzshipping.service.external;

import com.ecorp.fritzshipping.entity.Customer;
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
    
}
