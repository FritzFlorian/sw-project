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
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class TrackingPoint extends GeneratedLongIdEntity {
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedAt;
    
    @Enumerated(EnumType.STRING)
    private TrackingType type;
    
    public TrackingPoint() {
        // Empty Default Constructor
    }

    public TrackingPoint(Date finishedAt, TrackingType type) {
        this.finishedAt = finishedAt;
        this.type = type;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public TrackingType getType() {
        return type;
    }

    public void setType(TrackingType type) {
        this.type = type;
    }
}
