package com.ecorp.fritzshipping.service;

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
import org.apache.logging.log4j.Logger;

@RequestScoped
public class DeliveryService implements DeliveryServiceIF, Serializable {
    @PersistenceContext
    private EntityManager em;
    
    private final Random rand = new Random(); 
    
    @Inject
    private Logger logger;
    
    @Inject
    private MailHelperIF mailHelper;
    
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
        shipment.getTrackingNotifications().size();
        shipment.getTrackingPoints().size();
        
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
                try {
                    mailHelper.sendShipmentProgerssMail(shipment, currentTrackingPoint, trackingNotification.getEmail());
                } catch(WebServiceException e) {
                    logger.info("Could not send shipment update mail to new customer. Proceeding as it is not crucial, customer can check on status if needed.");
                }
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
}
