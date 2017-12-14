package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.GeneratedLongIdEntity;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Customer extends GeneratedLongIdEntity {
    @OneToMany(mappedBy="customer")
    private Collection<Order> orders;
    
    private long bankAccountId;
    @Column(unique=true)
    private String email;
    private String password;
    @Embedded
    private Address address;
    
    public Customer() {
        // Empty Default Constructor
        this.address = new Address();
    }

    public Collection<Order> getOrders() {
        return orders;
    }

    public void setOrders(Collection<Order> orders) {
        this.orders = orders;
    }

    public long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    
}
