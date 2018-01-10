package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.entity.Shipment;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.ecorp.fritzshipping.service.DeliveryServiceIF;

@Named
@RequestScoped
public class ShipmentPickupModel implements Serializable {
    @Inject
    private DeliveryServiceIF deliveryService;
    
    public List<Shipment> getShipmentsReadyForPickup() {
        return deliveryService.getShipmentsReadyForPickup();
    }
}
