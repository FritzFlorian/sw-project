package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.GeneratedLongIdEntity;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "shipment_order")
public class Order extends GeneratedLongIdEntity {
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    private long paymentTransactionId;
    private long totalPrice;
    
    @ManyToOne
    private Customer customer;
    
    @OneToMany
    private Collection<Shipment> shipments;
    
    public Order() {
        // Empty Default Constructor
    }

    public Order(Date createdAt, long paymentTransactionId, long totalPrice, Customer customer, Collection<Shipment> shipments) {
        this.createdAt = createdAt;
        this.paymentTransactionId = paymentTransactionId;
        this.totalPrice = totalPrice;
        this.customer = customer;
        this.shipments = shipments;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(long paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Collection<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(Collection<Shipment> shipments) {
        this.shipments = shipments;
    }
}
