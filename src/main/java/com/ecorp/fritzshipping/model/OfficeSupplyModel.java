/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.service.DeliveryServiceIF;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class OfficeSupplyModel implements Serializable {
    
    @Inject
    private DeliveryServiceIF deliveryService;
    
    public String orderOfficeSupply() {
        if (deliveryService.orderOfficeSupply()) {
            return "office-supply-success";
        } else {
            return "office-supply-error";
        }
    }
}
