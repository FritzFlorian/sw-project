/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecorp.fritzshipping.model;

import com.ecorp.fritzshipping.entity.ShipmentType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Gather utility methods.
 */
@Named
@ApplicationScoped
public class UtilityModel implements Serializable {
    public Collection<ShipmentType> getPossibleShipmentTypes() {
        return Arrays.asList(ShipmentType.values());
    }
}
