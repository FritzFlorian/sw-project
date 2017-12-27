/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.service.DeliveryIF;
import com.ecorp.fritzshipping.entity.Shipment;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.omnifaces.cdi.Param;
import javax.inject.Named;

@Named
@RequestScoped
public class ShipmentLabelModel implements Serializable {
    @Inject @Param(pathIndex=0)
    private String shipmentId;
    
    @Inject
    private DeliveryIF deliveryService;

    public Shipment getShipment() {
        return deliveryService.getShipment(shipmentId);
    }
    
}
