package com.ecorp.fritzshipping.service;

import com.ecorp.fritzshipping.entity.Shipment;

public class ShipmentException extends Exception {
    private Shipment shipment;

    public ShipmentException(Shipment shipment, String message) {
        super(message);
        this.shipment = shipment;
    }

    ShipmentException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }
}
