package com.ecorp.fritzshipping.entity.util;

import java.io.Serializable;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class SingleIdEntity<K> implements Serializable{
    
    public abstract K getId();
    
    @Override
    public int hashCode() {
        if(getId() == null)
            return 0;
        else
            return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }

        SingleIdEntity<K> other = (SingleIdEntity<K>) obj;

        if(this.getId()==null)
            return false;
        
        if(this.getId().equals(other.getId()))
            return true;

        return false;
    }
}
