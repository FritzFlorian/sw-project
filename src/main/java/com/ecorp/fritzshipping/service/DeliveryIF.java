package com.ecorp.fritzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingNotification;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import java.util.List;


public interface DeliveryIF {
    /**
     * Creates an shipment,
     * plans it's route and adds it's shipment steps.
     * 
     * @param shipment The shipment to be created.
     * @throws ShipmentException Thrown if the shipment violates constraints.
     * @return The newly created shipment.
     */
    public Shipment createShipment(Shipment shipment) 
            throws ShipmentException;
    
    /**
     * Load an shipment with the given id.
     * 
     * @param id The id of the shipment to be loaded.
     * @return The loaded shipment.
     */
    public Shipment getShipment(String id);
    
    /**
     * Register a new email address for updates on the shipment progress
     * of a single shipment.
     * 
     * @param shipment The shipment to receive updates of.
     * @param notification The notification target to be added to the shipment.
     */
    public void registerTrackingNotification(Shipment shipment, TrackingNotification notification);
    
    /**
     * Gets the next tracking point that has to be processed.
     * 
     * @param shipment The shipment of which to get the next tracking point.
     * @return The next tracking point to be processed or null if there is none.
     */
    public TrackingPoint getNextTrackingPoint(Shipment shipment);
    
    /**
     * Marks the given tracking point as processed after the
     * required work has been done.
     * 
     * @param trackingPoint The tracking point to be marked as processed.
     */
    public void processTrackingPoint(TrackingPoint trackingPoint);
    
    /**
     * Deletes a shipment and all it's associated entities.
     * 
     * @param shipment The shipment to be deleted.
     */
    public void deleteShipment(Shipment shipment);
    
    /**
     * Finds all shipments that have to be picked up at the customers address.
     * 
     * @return The shipments to be picked up.
     */
    public List<Shipment> getShipmentsReadyForPickup();
}
