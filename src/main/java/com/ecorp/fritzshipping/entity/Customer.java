package com.ecorp.fritzshipping.entity;

import com.ecorp.fritzshipping.entity.util.SingleIdEntity;
import java.util.Collections;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Customer extends SingleIdEntity<String> {
    // Use the email as ID, as it uniquly identifies customer accounts.
    // We use the single id entity by simply returning the email in
    // the getId() method.
    @Id
    private String email;
    
    @OneToMany(mappedBy="customer")
    private List<Order> orders;
    
    private long bankAccountId;
    private String password;
    @Embedded
    private Address address;
    
    public Customer() {
        // Empty Default Constructor
        this.address = new Address();
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }
    
    public void addOrder(Order order) {
        if (!orders.contains(order)) {
            orders.add(order);
        }
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

    @Override
    public String getId() {
        return this.email;
    }
}
