package com.ecorp.fritzshipping.model;

import com.ecorp.firtzshipping.service.CustomerServiceIF;
import com.ecorp.fritzshipping.entity.Customer;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class LoginModel implements Serializable{
    private Customer currentCustomer;
    private Customer loginScreenCustomer;
    
    @Inject
    private CustomerServiceIF customerService;
    
    public LoginModel() {
        this.currentCustomer = null;
        this.loginScreenCustomer = new Customer();
    }
    
    public String register() {
        Customer newCustomer =  customerService.createCustomer(loginScreenCustomer);
        return "registration-success";
    }

    public String login() {
        currentCustomer = customerService.login(loginScreenCustomer);
        if(currentCustomer == null) {
            return "login-failed";
        } else {
            loginScreenCustomer = new Customer();
            return "login-success";
        }
    }
    
    public String logout() {
        this.currentCustomer = null;
        return "logout";
    }
    
    public boolean isLoggedIn() {
        return this.currentCustomer != null;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public Customer getLoginScreenCustomer() {
        return loginScreenCustomer;
    }

    public void setLoginScreenCustomer(Customer loginScreenCustomer) {
        this.loginScreenCustomer = loginScreenCustomer;
    }
    
}
