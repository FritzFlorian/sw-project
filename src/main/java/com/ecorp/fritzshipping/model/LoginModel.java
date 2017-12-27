package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.entity.Customer;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import com.ecorp.firtzshipping.service.CustomerIF;

@Named
@SessionScoped
public class LoginModel implements Serializable{
    private Customer currentCustomer;
    private Customer loginScreenCustomer;
    
    @Inject
    private CustomerIF customerService;
    
    public LoginModel() {
        this.currentCustomer = null;
        this.loginScreenCustomer = new Customer();
    }
    
    public String register() {
        customerService.createCustomer(loginScreenCustomer);
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
        // User logout means no session data should be valid anymore.
        // The best practice here seems to be to invalidate/destroy all session
        // scoped beans instead of setting all data to null.
        // There is also HttpServletRequest.logout(), but this interacts with
        // JavaEE security context (we did not learn how to use them and this
        // project uses simple session models for login, so i think it's fine
        // not to use it for this purpose).
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
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
