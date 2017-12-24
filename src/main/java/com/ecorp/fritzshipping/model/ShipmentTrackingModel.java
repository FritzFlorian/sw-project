/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecorp.fritzshipping.model;

import com.ecorp.firtzshipping.service.DeliveryIF;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.TrackingPoint;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.omnifaces.cdi.Param;
import javax.inject.Named;

@Named
@RequestScoped
public class ShipmentTrackingModel {
    @Inject @Param(name="shipmentId")
    private String shipmentId;
    
    @Inject
    private DeliveryIF deliveryService;
    
    private String email;
    private boolean onlyLast;

    public Shipment getShipment() {
        return deliveryService.getShipment(shipmentId);
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
        onlyLast = false;
        email = "";
        
        return "#";
    }
    
    public String getProgress() {
        int finished = 0;
        List<TrackingPoint> trackingPoints = getShipment().getTrackingPoints();
        for (TrackingPoint point : trackingPoints) {
            if (point.getFinishedAt() != null) {
                finished++;
            }
        }
        
        return "" + ((float)finished / trackingPoints.size()) * 100;
    }
    
}
