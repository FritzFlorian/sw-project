package com.ecorp.fritzshipping.service;

import com.ecorp.buero.service.OrderServiceService;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingNotification;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import com.ecorp.fritzshipping.entity.TrackingType;
import com.ecorp.fritzshipping.service.external.MailHelperIF;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceRef;
import org.apache.logging.log4j.Logger;

@RequestScoped
public class DeliveryService implements DeliveryServiceIF, Serializable {
    private static final int OFFICE_SUPPLY_ACCOUNT_ID = 1001;
    private static final int BOX_ARTICLE_ID = 2001;
    
    @PersistenceContext
    private EntityManager em;
    
    private final Random rand = new Random(); 
    
    @Inject
    private Logger logger;
    
    @Inject
    private MailHelperIF mailHelper;
    
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/im-lamport.othr.de_8080//blockstiftundradierer/OrderService.wsdl")
    private OrderServiceService officeSupplyServiceRef;
    
    @Override
    @Transactional
    public Shipment createShipment(Shipment shipment) throws ShipmentException {
        // Simulate some business logic. For this a simple check is enough.
        if (shipment.getWeight() > shipment.getType().getMaxWeight()) {
            throw new ShipmentException(shipment, "Shipment is too heavy!");
        }
        
        // Plan the route for the shipment and persist it.
        // Again this is simplified business logic.
        planRouteForShipment(shipment);
        // Good example of the CascadeType.Persist on the trackingPoints.
        // We added them to the shipment WITHOUT persisting them, but
        // the cascade should now persist them together with the shipment.
        em.persist(shipment);
        
        logger.debug("Shipment Created.");
        return shipment;
    }
    
    private void planRouteForShipment(Shipment shipment) {
        List<TrackingPoint> trackingPoints = new LinkedList<>();
        
        // Start with pickup vs hand in
        if (shipment.isPickup()) {
            trackingPoints.add(new TrackingPoint(TrackingType.PICKUP));
        } else {
            trackingPoints.add(new TrackingPoint(TrackingType.HAND_IN));
        }
        
        // Add some random intermidiate steps
        for (int i = 0; i < rand.nextInt(2) + 1; i++) {
            trackingPoints.add(new TrackingPoint(TrackingType.PACKAGE_CENTER));
        }
        
        // Finally we deliver the shipment
        trackingPoints.add(new TrackingPoint(TrackingType.DELIVERY));
        
        shipment.setTrackingPoints(trackingPoints);
    }
    

    @Override
    @Transactional
    public Shipment getShipment(String id) {
        Shipment shipment = em.find(Shipment.class, id);
        
        // When calling to explicitly get a shipment we also
        // want to load its associations, so preload them.
        // This is done by calling them, as FETCH on multiple
        // collections causes problems.
        if (shipment != null) {
            shipment.getTrackingNotifications().size();
            shipment.getTrackingPoints().size();
        }
        
        return shipment;
    }
    
    @Override
    @Transactional
    public void registerTrackingNotification(Shipment shipment, TrackingNotification notification) {
        // Here we do not use CascadeType.Persist, thus we have to
        // manually persist the notification and then add it to the
        // attatched shipment's notifications.
        Shipment loadedShipment = em.find(Shipment.class, shipment.getId());
        em.persist(notification);
        loadedShipment.getTrackingNotifications().add(notification);
    }

    @Override
    public TrackingPoint getNextTrackingPoint(Shipment shipment) {
        for (TrackingPoint trackingPoint : shipment.getTrackingPoints()) {
            if (!trackingPoint.isFinished()) {
                return trackingPoint;
            }
        }
        
        return null;
    }
    
    @Override
    public TrackingPoint getCurrentTrackingPoint(Shipment shipment) {
        List<TrackingPoint> trackingPoints = shipment.getTrackingPoints();
        for (int i = 0; i < trackingPoints.size() - 1; i++) {
            if (!trackingPoints.get(i + 1).isFinished()) {
                return trackingPoints.get(i);
            }
        }
        
        return trackingPoints.get(trackingPoints.size() - 1);
    }

    @Override
    @Transactional
    public void processNextTrackingPoint(Shipment shipment) {
        TrackingPoint loadedTrackingPoint = em.find(TrackingPoint.class, getNextTrackingPoint(shipment).getId());
        loadedTrackingPoint.setFinishedAt(new Date());
        
        // We attatch the shipment to be sure all added tracking points are there.
        sendShipmentProcessEmailNotification(getShipment(shipment.getId()));
    }
    
    private void sendShipmentProcessEmailNotification(Shipment shipment) {
        TrackingPoint currentTrackingPoint = getCurrentTrackingPoint(shipment);
        boolean lastPoint = currentTrackingPoint.getType() == TrackingType.DELIVERY;
        
        for (TrackingNotification trackingNotification : shipment.getTrackingNotifications()) {
            if (!trackingNotification.isOnlyLastPoint() || lastPoint) {
                mailHelper.sendShipmentProgerssMail(shipment, currentTrackingPoint, trackingNotification.getEmail());
            }
        }
    }

    @Override
    @Transactional
    public void deleteShipment(Shipment shipment) {
        Shipment loadedShipment = em.find(Shipment.class, shipment.getId());
        em.remove(loadedShipment);
    }

    @Override
    public List<Shipment> getShipmentsReadyForPickup() {
        TypedQuery<Shipment> query = em.createNamedQuery("Shipment.readyForPickup", Shipment.class);
        return query.getResultList();
    } 

    
    @Override
    public boolean orderOfficeSupply() {
        // This is mostly a placeholder to use the office supply service.
        com.ecorp.buero.service.OrderService officeSupplyService = officeSupplyServiceRef.getOrderServicePort();
        com.ecorp.buero.service.Customer customer = new com.ecorp.buero.service.Customer();
        customer.setId(OFFICE_SUPPLY_ACCOUNT_ID);
        
        com.ecorp.buero.service.Article boxArticle = new com.ecorp.buero.service.Article();
        boxArticle.setId(BOX_ARTICLE_ID);
        
        try {
            officeSupplyService.alterShoppingCart(customer, boxArticle, 20);
            officeSupplyService.buyShoppingCart(customer);
        } catch(com.ecorp.buero.service.Exception_Exception e) {
            logger.warn("Could not order office supplies (exception in service)." + e.getMessage());
            return false;
        } catch(WebServiceException e) {
            logger.warn("Could not order office supplies (timeout in service). " + e.getMessage());
            return false;
        }
        
        
        return true;
    }  
}
