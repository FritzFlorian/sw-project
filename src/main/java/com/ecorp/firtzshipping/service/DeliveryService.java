package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingNotification;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import com.ecorp.fritzshipping.entity.TrackingType;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@RequestScoped
public class DeliveryService implements DeliveryIF{
    @PersistenceContext
    private EntityManager em;
    
    private Random rand = new Random(); 
    
    @Override
    @Transactional
    public Shipment createShipment(Shipment shipment) throws ShipmentException {
        // Simulate some business logic. For this a simple check is enough.
        if (shipment.getWeight() > shipment.getType().getMaxWeight()) {
            throw new ShipmentException(shipment, "Shipment is too heavy!");
        }
        
        em.persist(shipment);
        // Plan the route for the shipment and persist it.
        // Again this is simplified business logic.
        planRouteForShipment(shipment);
        
        return shipment;
    }
    
    @Transactional
    private void planRouteForShipment(Shipment shipment) {
        List<TrackingPoint> trackingPoints = new LinkedList<>();
        
        // Start with pickup vs hand in
        if (shipment.isPickup()) {
            trackingPoints.add(createTrackingPoint(TrackingType.PICKUP));
        } else {
            trackingPoints.add(createTrackingPoint(TrackingType.HAND_IN));
        }
        
        // Add some random intermidiate steps
        for (int i = 0; i < rand.nextInt(2) + 1; i++) {
            trackingPoints.add(createTrackingPoint(TrackingType.PACKAGE_CENTER));
        }
        
        // Finally we deliver the shipment
        trackingPoints.add(createTrackingPoint(TrackingType.DELIVERY));
        
        shipment.setTrackingPoints(trackingPoints);
    }
    
    @Transactional
    private TrackingPoint createTrackingPoint(TrackingType type) {
        TrackingPoint newPoint = new TrackingPoint(null, type);
        em.persist(newPoint);
        return newPoint;
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
}
