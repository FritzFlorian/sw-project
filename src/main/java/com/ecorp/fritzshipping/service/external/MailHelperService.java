package com.ecorp.fritzshipping.service.external;

import com.ecorp.fritzshipping.entity.Customer;
import com.ecorp.gorillamail.services.Header;
import com.ecorp.gorillamail.services.Mail;
import com.ecorp.gorillamail.services.MailException_Exception;
import com.ecorp.gorillamail.services.MailService;
import com.ecorp.gorillamail.services.MailServiceService;
import com.ecorp.gorillamail.services.Template;
import com.ecorp.gorillamail.services.User;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.xml.ws.WebServiceRef;
import org.apache.logging.log4j.Logger;

// Go for dependent, as webservices are not thread save.
@Dependent
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
        mail.setAd(true);
        mail.setTemplate(template);
        
        Header toHeader = new Header();
        toHeader.setName("to");
        toHeader.setValue(customer.getEmail());
        Header subjectHeader = new Header();
        subjectHeader.setName("subject");
        subjectHeader.setValue("Fritzshipping Registration Success");
        
        mail.getHeaders().add(toHeader);
        mail.getHeaders().add(subjectHeader);
        
        try {
            mailService.sendMail(mailServiceUser, mail);
        } catch (MailException_Exception ex) {
            logger.warn("Failed to send registration email to {}. Error in MailService.", customer.getEmail());
        }
    }  
}
