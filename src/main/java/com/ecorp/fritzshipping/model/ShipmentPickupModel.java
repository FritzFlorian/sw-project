package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.service.DeliveryIF;
import com.ecorp.fritzshipping.entity.Shipment;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class ShipmentPickupModel implements Serializable {
    @Inject
    private DeliveryIF deliveryService;
    
    public List<Shipment> getShipmentsReadyForPickup() {
        return deliveryService.getShipmentsReadyForPickup();
    }
}
