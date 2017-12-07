package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.RandomUUIDEntity;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Shipment extends RandomUUIDEntity{
    private int weight;
    private boolean pickup;
    
    @OneToMany
    private Collection<TrackingPoint> trackingPoints;
    @OneToMany
    private Collection<TrackingNotification> trackingNotifications;
    
    public Shipment() {
        // Empty Default Constructor
    }

    public Shipment(int weight, boolean pickup, Collection<TrackingPoint> trackingPoints, Collection<TrackingNotification> trackingNotifications) {
        this.weight = weight;
        this.pickup = pickup;
        this.trackingPoints = trackingPoints;
        this.trackingNotifications = trackingNotifications;
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

    public Collection<TrackingPoint> getTrackingPoints() {
        return trackingPoints;
    }

    public void setTrackingPoints(Collection<TrackingPoint> trackingPoints) {
        this.trackingPoints = trackingPoints;
    }

    public Collection<TrackingNotification> getTrackingNotifications() {
        return trackingNotifications;
    }

    public void setTrackingNotifications(Collection<TrackingNotification> trackingNotifications) {
        this.trackingNotifications = trackingNotifications;
    }
    
    
}
