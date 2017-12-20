package com.ecorp.firtzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;

class ShipmentException extends Exception {
    private Shipment shipment;

    public ShipmentException(Shipment shipment, String message) {
        super(message);
        this.shipment = shipment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
}
