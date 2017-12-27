package com.ecorp.fritzshipping.util;

import javax.enterprise.context.Dependent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

// We need an annotation here, otherwise it won't be found for injection.
// Dependent seems like a good scope, as the logger is needed for the
// lifecicle of the object it is injected to.
// (Often @Singleton is used here, wich makes sense (we only really want
//  to call the method, the object is not relevant). But I will not use it,
//  as I want to avoid mixing EJB into the otherwise pure CDI injection.)
@Dependent
public class LoggerFactory {
    @Produces
    public Logger produceLogger(InjectionPoint injectionPoint) {
        // Inject a logger in other classes.
        // There will be one logger per class.
        return LogManager.getLogger(injectionPoint.getMember().getDeclaringClass());  
    }
}
