package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.service.ShipmentException;
import com.ecorp.fritzshipping.entity.Shipment;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.ecorp.fritzshipping.service.DeliveryServiceIF;

@Named
@ConversationScoped
public class ShipmentCreationModel implements Serializable {
    private Shipment shipment;
    private ShipmentException lastException;
    
    @Inject
    private Conversation conversation;
    
    @Inject
    private DeliveryServiceIF deliveryService;
    
    public ShipmentCreationModel() {
        this.shipment = new Shipment();
        this.lastException = null;
    }
    
    @PostConstruct
    public void postConstruct() {
        // Conversation begins when a user visits the page with this model.
        // It will end after the shipment was created.
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }
    
    /**
     * Create the shipment from the current inputs
     * and persist it.
     * 
     * @return The next page to be displayed.
     */
    public String createShipment() {
        try {
            shipment = deliveryService.createShipment(shipment);
            lastException = null;
            
            return "shipment-creation-success";
        } catch (ShipmentException e) {
            lastException = e;
            return "shipment-creation-error";
        }
    }
    
    /**
     * Confirming the shipment will mark it as received at the post station.
     * 
     * @return The next page to be displayed.
     */
    public String confirmShipment() {
        deliveryService.processNextTrackingPoint(shipment);
        if (!conversation.isTransient()) {
            conversation.end();
        }
        
        return "shipment-confirmed";
    }
    
    /**
     * Canceling the shipment will delete it again from the database.
     * This is mostly to have one case where we actually have to delete
     * an entity form the database.
     * 
     * @return The next page to be displayed.
     */
    public String cancelShipment() {
        deliveryService.deleteShipment(shipment);
        if (!conversation.isTransient()) {
            conversation.end();
        }
        
        return "shipment-canceled";
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public ShipmentException getLastException() {
        return lastException;
    }

    public void setLastException(ShipmentException lastException) {
        this.lastException = lastException;
    }
}
