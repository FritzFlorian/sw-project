package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;


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
}
