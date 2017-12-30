/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.GeneratedLongIdEntity;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class TrackingPoint extends GeneratedLongIdEntity {
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedAt;
    
    @Enumerated(EnumType.STRING)
    private TrackingType type;
    
    public TrackingPoint() {
        // Empty Default Constructor
    }

    public TrackingPoint(TrackingType type) {
        this.finishedAt = null;
        this.type = type;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }
    
    public boolean isFinished() {
        return this.finishedAt != null;
    }

    public TrackingType getType() {
        return type;
    }

    public void setType(TrackingType type) {
        this.type = type;
    }
}
