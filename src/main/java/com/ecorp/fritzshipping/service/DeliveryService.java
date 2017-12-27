package com.ecorp.fritzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingNotification;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import com.ecorp.fritzshipping.entity.TrackingType;
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
import org.apache.logging.log4j.Logger;

@RequestScoped
public class DeliveryService implements DeliveryIF{
    @PersistenceContext
    private EntityManager em;
    
    private final Random rand = new Random(); 
    
    @Inject
    private Logger logger;
    
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
    
    @Transactional
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
        TypedQuery<Shipment> query =
                em.createQuery("SELECT s "
                             + "FROM Shipment s "
                             + "LEFT JOIN FETCH s.trackingPoints "
                             + "WHERE s.id =:id", Shipment.class);
        query.setParameter("id", id);
        
        return query.getSingleResult();
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
    @Transactional
    public TrackingPoint getNextTrackingPoint(Shipment shipment) {
        TypedQuery<TrackingPoint> query = em.createNamedQuery("TrackingPoint.nextForShipment", TrackingPoint.class);
        query.setParameter("shipmentId", shipment.getId());
        query.setMaxResults(1);
        
        List<TrackingPoint> results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    @Transactional
    public void processTrackingPoint(TrackingPoint trackingPoint) {
        TrackingPoint loadedTrackingPoint = em.find(TrackingPoint.class, trackingPoint.getId());
        loadedTrackingPoint.setFinishedAt(new Date());
    }

    @Override
    @Transactional
    public void deleteShipment(Shipment shipment) {
        Shipment loadedShipment = em.find(Shipment.class, shipment.getId());
        em.remove(loadedShipment);
    }

    @Override
    @Transactional
    public List<Shipment> getShipmentsReadyForPickup() {
        TypedQuery<Shipment> query = em.createNamedQuery("Shipment.readyForPickup", Shipment.class);
        return query.getResultList();
    }
    
    
}
