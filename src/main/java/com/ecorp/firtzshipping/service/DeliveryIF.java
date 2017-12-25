package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingNotification;


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
}
