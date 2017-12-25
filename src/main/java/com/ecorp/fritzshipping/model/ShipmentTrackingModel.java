/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecorp.fritzshipping.model;

import com.ecorp.firtzshipping.service.DeliveryIF;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingNotification;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.omnifaces.cdi.Param;
import javax.inject.Named;

@Named
@RequestScoped
public class ShipmentTrackingModel implements Serializable {
    @Inject @Param(pathIndex=0)
    private String shipmentId;
    private Shipment shipment;
    
    @Inject
    private DeliveryIF deliveryService;
    
    private String email;
    private boolean onlyLast;
    
    
    @PostConstruct
    public void postConstruct() {
        // Try out post construct hooks in this model as it plays
        // nicely with the request parameter.
        if (shipmentId != null && !shipmentId.isEmpty()) {
            shipment = deliveryService.getShipment(shipmentId);
        }
        
        // Note:
        // On a form submission on this page the 'shipmentId' will be
        // used to reload the shipment (we do not store a session).
        // This behaviour is desired, as we do not want to create a
        // session on our server for every single user that only visits
        // the page to check on the shipment progress.
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnlyLast() {
        return onlyLast;
    }

    public void setOnlyLast(boolean onlyLast) {
        this.onlyLast = onlyLast;
    }
    
    public String registerForShipmentUpdates() {
        TrackingNotification notification = 
                new TrackingNotification(email, onlyLast);
        deliveryService.registerTrackingNotification(shipment, notification);

        return "update-registration-success";
    }
    
    public String getProgress() {
        int finished = 0;
        List<TrackingPoint> trackingPoints = shipment.getTrackingPoints();
        for (TrackingPoint point : trackingPoints) {
            if (point.getFinishedAt() != null) {
                finished++;
            }
        }
        
        return "" + ((float)finished / trackingPoints.size()) * 100;
    }
    
}
