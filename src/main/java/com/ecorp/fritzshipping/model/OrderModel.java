package com.ecorp.fritzshipping.model;

import com.ecorp.firtzshipping.service.CustomerServiceIF;
import com.ecorp.firtzshipping.service.ShipmentException;
import com.ecorp.fritzshipping.entity.Order;
import com.ecorp.fritzshipping.entity.Shipment;
import com.ecorp.fritzshipping.entity.ShipmentType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;


@Named
@SessionScoped
public class OrderModel implements Serializable{
    @Inject
    private CustomerServiceIF customerService;
    @Inject
    private LoginModel loginModel;
    
    // Keep the shipments of the currently created order.
    private List<Shipment> shipmentsOfCurrentOrder;
    private Shipment newShipment;
    private ShipmentException lastException;
    
    // Keep the currently selected order (detail view).
    private Order currentOrder;
    
    public OrderModel() {
        newShipment = new Shipment();
        shipmentsOfCurrentOrder = new LinkedList<>();
    }
    
    public List<Order> getAllOrders() {
        // We explicitly load the orders on login.
        // Instead of directly accessing them we still return them
        // with this method, so we could add lazy loading later.
        return loginModel.getCurrentCustomer().getOrders();
    }
    
    public List<Shipment> getShipmentsOfCurrentOrder() {
        numberShipmentsOfCurrentOrder();
        return shipmentsOfCurrentOrder;
    }
    
    /**
     * Gives all current shipments generated/artificial
     * primary IDs. This is needed to allow deletion of
     * list elements and is only temporary.
     */
    private void numberShipmentsOfCurrentOrder() {
        for (int i = 0; i < shipmentsOfCurrentOrder.size(); i++) {
            shipmentsOfCurrentOrder.get(i).setId("artificial-id-" + i);
        }
    }
    
    public String addShipmentToCurrentOrder() {
        shipmentsOfCurrentOrder.add(newShipment);
        newShipment = new Shipment();
        
        return "shipment-added";
    }
    
    public String cancelCurrentOrder() {
        newShipment = new Shipment();
        shipmentsOfCurrentOrder.clear();
        
        return "order-canceled";
    }
    
    public String placeCurrentOrder() {
        try {
            customerService.placeOrder(loginModel.getCurrentCustomer(), shipmentsOfCurrentOrder);
            newShipment = new Shipment();
            shipmentsOfCurrentOrder.clear();
            lastException = null;
            
            return "order-creation-success";
        } catch (ShipmentException e) {
            lastException = e;
            return "order-creation-error";
        }
    }
    
    public String removeShipmentFromCurrentOrder(Shipment shipment) {
        shipmentsOfCurrentOrder.remove(shipment);
        
        return "shipment-removed";
    }
    
    public Shipment getNewShipment() {
        return newShipment;
    }
    
    public Collection<ShipmentType> getPossibleShipmentTypes() {
        return Arrays.asList(ShipmentType.values());
    }

    public ShipmentException getLastException() {
        return lastException;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }
    
    public String viewOrderDetails(Order order) {
        currentOrder = customerService.getOrder(order.getId());
        
        return "order-details";
    }
    
}
