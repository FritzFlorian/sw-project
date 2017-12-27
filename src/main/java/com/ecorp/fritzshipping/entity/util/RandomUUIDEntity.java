package com.ecorp.fritzshipping.entity.util;


import javax.persistence.MappedSuperclass;
import javax.persistence.Id;
import static java.util.UUID.randomUUID;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)  // We want to include ids in our service
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
    
    public void setId(String id) {
        this.id = id;
    }
}
