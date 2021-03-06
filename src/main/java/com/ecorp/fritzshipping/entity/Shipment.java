package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.RandomUUIDEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@NamedQuery(name="Shipment.readyForPickup",
            query="Select s From Shipment s JOIN s.trackingPoints track WHERE track.type=com.ecorp.fritzshipping.entity.TrackingType.PICKUP AND track.finishedAt is null")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Shipment extends RandomUUIDEntity {
    private int weight;
    private boolean pickup;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="addressee",column=@Column(name="senderAddressee")),
        @AttributeOverride(name="street",column=@Column(name="senderStreet")),
        @AttributeOverride(name="houseNumber",column=@Column(name="senderHouseNumber")),
        @AttributeOverride(name="city",column=@Column(name="senderCity")),
        @AttributeOverride(name="postalCode",column=@Column(name="senderPostalCode")),
    })
    private Address sender;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="addressee",column=@Column(name="recipientAddressee")),
        @AttributeOverride(name="street",column=@Column(name="recipientStreet")),
        @AttributeOverride(name="houseNumber",column=@Column(name="recipientHouseNumber")),
        @AttributeOverride(name="city",column=@Column(name="recipientCity")),
        @AttributeOverride(name="postalCode",column=@Column(name="recipientPostalCode")),
    })
    private Address recipient;
    
    @Enumerated(EnumType.STRING)
    private ShipmentType type;
    
    // Orphal removal makes sense on both, as it makes
    // deleting shipments trivial.
    // We will use cascading for persisting only on one of two
    // to test out the difference it makes to using the relation.
    // (See DeliveryService for the example usages)
    @XmlTransient
    @OneToMany(orphanRemoval = true, cascade=CascadeType.PERSIST)
    private List<TrackingPoint> trackingPoints;
    @XmlTransient
    @OneToMany(orphanRemoval = true)
    private Set<TrackingNotification> trackingNotifications;
    
    public Shipment() {
        this.sender = new Address();
        this.recipient = new Address();
        this.trackingPoints = new ArrayList<>();
        this.trackingNotifications = new HashSet<>();
    }

    public Shipment(int weight, boolean pickup, Address sender, 
            Address recipient, List<TrackingPoint> trackingPoints, 
            Set<TrackingNotification> trackingNotifications,
            ShipmentType type) {
        this.weight = weight;
        this.pickup = pickup;
        this.sender = sender;
        this.recipient = recipient;
        this.trackingPoints = trackingPoints;
        this.trackingNotifications = trackingNotifications;
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isPickup() {
        return pickup;
    }

    public void setPickup(boolean pickup) {
        this.pickup = pickup;
    }

    public List<TrackingPoint> getTrackingPoints() {
        return trackingPoints;
    }

    public void setTrackingPoints(List<TrackingPoint> trackingPoints) {
        this.trackingPoints = trackingPoints;
    }

    public Collection<TrackingNotification> getTrackingNotifications() {
        return trackingNotifications;
    }

    public void setTrackingNotifications(Set<TrackingNotification> trackingNotifications) {
        this.trackingNotifications = trackingNotifications;
    }

    public Address getSender() {
        return sender;
    }

    public void setSender(Address sender) {
        this.sender = sender;
    }

    public Address getRecipient() {
        return recipient;
    }

    public void setRecipient(Address recipient) {
        this.recipient = recipient;
    }

    public ShipmentType getType() {
        return type;
    }

    public void setType(ShipmentType type) {
        this.type = type;
    }
    
    public int getPrice() {
        // Could add some logic for prices based on address distance.
        return this.type.getPrice();
    }
}
