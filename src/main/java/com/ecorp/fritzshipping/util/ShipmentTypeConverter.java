package com.ecorp.fritzshipping.util;

import com.ecorp.fritzshipping.entity.ShipmentType;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@RequestScoped
@FacesConverter("com.ecorp.fritzshipping.util.ShipmentTypeConverter")
public class ShipmentTypeConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        }
        
        try {
            return ShipmentType.valueOf(value);
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        }
        
        if (!value.getClass().equals(ShipmentType.class)) {
            return null;
        }
        
        return ((ShipmentType)value).toString();
    }
    
}
