package com.ecorp.fritzshipping.service.external;

import com.ecorp.bannerad.service.Confirmation;
import com.ecorp.bannerad.service.Customer;
import com.ecorp.bannerad.service.Dimension;
import com.ecorp.bannerad.service.OrderService;
import com.ecorp.bannerad.service.OrderService_Service;
import com.ecorp.bannerad.service.PlacingOrder;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceRef;
import org.apache.logging.log4j.Logger;


@ApplicationScoped
public class AdHelper implements AdHelperIF {
    // Placeholder when there is no banner from the ad service
    private static final String DEFAULT_BANNER_URL = "http://via.placeholder.com/468x60";
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/im-lamport_8080/banner_ad-1.0-SNAPSHOT/OrderService.wsdl")
    private OrderService_Service serviceRef;
    private OrderService service;
    
    @Inject
    private Logger logger;
    
    @PostConstruct
    public void postConstruct() {
        service = serviceRef.getOrderServicePort();
    }
    
    @Override
    public String getHorizontalBanner() {
        Customer customer = new Customer();
        customer.setEmail("flo.fritz@t-online.de");

        PlacingOrder order = new PlacingOrder();
        Dimension dimension = new Dimension();
        dimension.setHeight(60);
        dimension.setWidth(468);
        order.setDimension(dimension);
        
        try {
            Confirmation confirmation = service.orderBannerPlacement(customer, order);
            
            if (confirmation.getUrls().isEmpty()) {
                // Ignore and simply do not show banner
                logger.debug("Ad Service did not provide banner!");
                return DEFAULT_BANNER_URL;
            } else {
                logger.debug("Ad Service did provide banner.");
                return confirmation.getUrls().get(0);
            }
        } catch(WebServiceException e) {
            // Ignore and simply do not show banner
            logger.debug("Ad Service time out!");
            return DEFAULT_BANNER_URL;
        }
    }
    
}
