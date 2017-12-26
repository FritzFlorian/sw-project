package com.ecorp.fritzshipping.model;

import com.ecorp.firtzshipping.service.DeliveryIF;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;


@Named
@ConversationScoped
public class ShipmentProcessingModel implements Serializable {
    @Inject
    private Conversation conversation;
    
    private String shipmentId;
    private Shipment shipment;
    
    @Inject
    private DeliveryIF deliveryService;
    
    @PostConstruct
    public void postConstruct() {
        // Processing one single shipment is a conversation.
        // This starts with the first request to the model.
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
        this.shipment = deliveryService.getShipment(shipmentId);
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
    
    public TrackingPoint getNextTrackingPoint() {
        return deliveryService.getNextTrackingPoint(shipment);
    }

    public String startProcessingShipment() {
        return "start-processing-shipment";
    }
    
    public String finishProcessingShipment() {
        deliveryService.processTrackingPoint(getNextTrackingPoint());
        conversation.end();
        
        return "finish-processing-shipment";
    }
    
    public String skipProcessingShipment() {
        conversation.end();
        
        return "skip-processing-shipment";
    }
}
