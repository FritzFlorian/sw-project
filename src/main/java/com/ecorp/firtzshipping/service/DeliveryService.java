package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnit;

@RequestScoped
public class DeliveryService implements DeliveryIF{
    @PersistenceUnit
    private EntityManager em;
    
    @Override
    public Shipment createShipment(Shipment shipment) throws ShipmentException {
        // Simulate some business logic. For this a simple check is enough.
        if (shipment.getWeight() > shipment.getType().getMaxWeight()) {
            throw new ShipmentException(shipment, "Shipment is too heavy!");
        }
        
        em.persist(shipment);
        // Plan the route for the shipment and persist it.
        // Again this is simplified business logic.
        
        return shipment;
    }
    
}
