package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.GeneratedLongIdEntity;
import javax.persistence.Entity;

@Entity
public class TrackingNotification extends GeneratedLongIdEntity{
    private String email;
    private boolean onlyLastPoint;
    
    public TrackingNotification(){ 
        // Empty Default Constructor
    }

    public TrackingNotification(String email, boolean onlyLastPoint) {
        this.email = email;
        this.onlyLastPoint = onlyLastPoint;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnlyLastPoint() {
        return onlyLastPoint;
    }

    public void setOnlyLastPoint(boolean onlyLastPoint) {
        this.onlyLastPoint = onlyLastPoint;
    }
}
