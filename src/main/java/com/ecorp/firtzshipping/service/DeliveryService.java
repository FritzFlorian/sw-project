package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@RequestScoped
public class DeliveryService implements DeliveryIF{
    @PersistenceContext
    private EntityManager em;
    
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
        
        return shipment;
    }

    @Override
    public Shipment getShipment(String id) {
        return em.find(Shipment.class, id);
    }
    
}
