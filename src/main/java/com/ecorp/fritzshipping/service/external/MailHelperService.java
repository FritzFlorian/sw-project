package com.ecorp.fritzshipping.service.external;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import com.ecorp.gorillamail.services.Header;
import com.ecorp.gorillamail.services.Mail;
import com.ecorp.gorillamail.services.MailException_Exception;
import com.ecorp.gorillamail.services.MailService;
import com.ecorp.gorillamail.services.MailServiceService;
import com.ecorp.gorillamail.services.Template;
import com.ecorp.gorillamail.services.User;
import com.ecorp.gorillamail.services.Variable;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.xml.ws.WebServiceRef;
import org.apache.logging.log4j.Logger;

@ApplicationScoped
@Alternative
public class MailHelperService implements MailHelperIF, Serializable {
    // Not really 'nice', but good enoug for our purposes.
    private static final String USER_EMAIL = "flo.fritz@t-online.de";
    private static final String USER_PASSWORD = "123";
    private static final long MAIL_TEMPLATE_ID = 3;
    
    // Inject our mail service port
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/im-lamport_8080/gorillamail/MailService.wsdl")
    private MailServiceService mailServiceRef;
    private MailService mailService;
    
    private User mailServiceUser;
    
    @Inject
    private Logger logger;
    
    @PostConstruct
    public void postConstruct() {
        mailService = mailServiceRef.getMailServicePort();
        
        mailServiceUser = new User();
        mailServiceUser.setEmail(USER_EMAIL);
        mailServiceUser.setPassword(USER_PASSWORD);
    }

    @Override 
    public void sendRegistrationMail(Customer customer) {
        Template template = new Template();
        template.setId(MAIL_TEMPLATE_ID);
        
        Mail mail = new Mail();
        mail.setAd(false);
        mail.setTemplate(template);

        mail.getHeaders().add(makeHeader("to", customer.getEmail()));
        mail.getHeaders().add(makeHeader("subject", "Fritzshipping Registration Success"));
        
        try {
            mailService.sendMail(mailServiceUser, mail);
        } catch (MailException_Exception ex) {
            logger.warn("Failed to send registration email to {}. Error in MailService.", customer.getEmail());
        }
    }

    @Override
    public void sendShipmentProgerssMail(Shipment shipment, TrackingPoint trackingPoint, String email) {
        Template template = new Template();
        template.setId(MAIL_TEMPLATE_ID);
        
        Mail mail = new Mail();
        mail.setAd(true);
        mail.setTemplate(template);
        
        mail.getHeaders().add(makeHeader("to", email));
        mail.getHeaders().add(makeHeader("subject", "Fritzshipping Shipment Update"));
        
        mail.getVariables().add(makeVariable("shipmentId", shipment.getId()));
        mail.getVariables().add(makeVariable("status", trackingPoint.getType().toString()));
        
        try {
            mailService.sendMail(mailServiceUser, mail);
        } catch (MailException_Exception ex) {
            logger.warn("Failed to send shipment update '{}' to '{}'. ({}) Error in MailService.", shipment.getId(), email, trackingPoint.getType());
        }
    }
    
    private Variable makeVariable(String name, String value) {
        Variable newVariable = new Variable();
        newVariable.setName(name);
        newVariable.setValue(value);
        
        return newVariable;
    }
    
    private Header makeHeader(String name, String value) {
        Header newHeader = new Header();
        newHeader.setName(name);
        newHeader.setValue(value);
        
        return newHeader;
    }
}
