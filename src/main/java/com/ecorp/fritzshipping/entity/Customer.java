package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.GeneratedLongIdEntity;
import java.util.Collection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Customer extends GeneratedLongIdEntity {
    @OneToMany(mappedBy="customer")
    private Collection<Order> orders;
    
    private long bankAccountId;
    private String email;
    private String password;
    @Embedded
    private Address address;
    
    public Customer() {
        // Empty Default Constructor
    }
    
    
}
