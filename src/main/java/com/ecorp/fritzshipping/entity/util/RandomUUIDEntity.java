package com.ecorp.fritzshipping.entity.util;


import javax.persistence.MappedSuperclass;
import javax.persistence.Id;
import static java.util.UUID.randomUUID;

@MappedSuperclass
public abstract class RandomUUIDEntity extends SingleIdEntity<String> {

    @Id
    protected String id;
    
    // Nutzt die Java-Implementierung des UUID-Algorithmus
    protected RandomUUIDEntity(){
        this.id = randomUUID().toString();
    }
    
    @Override
    public String getId() {
        return this.id;
    }

}
