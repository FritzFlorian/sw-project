package com.ecorp.fritzshipping.entity.util;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)  // We want to include ids in our service
public abstract class GeneratedLongIdEntity extends SingleIdEntity<Long> {
    
    @Id 
    @GeneratedValue(strategy=GenerationType.AUTO)
    protected Long id;

    @Override
    public Long getId() {
        return this.id;
    }
}
