package com.ecorp.fritzshipping.entity;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Different types of shipments have different restrictions,
 * costs etc.
 */
@XmlEnum(String.class)
public enum ShipmentType {
    LETTER,
    SMALL_PARCEL,
    PARCEL,
    PACKAGE;
    
    private int price;
    private int maxWeight;
    
    static {
        LETTER.price = 60;
        SMALL_PARCEL.price = 4_50;
        PARCEL.price = 6_50;
        PACKAGE.price = 9_50;
        
        LETTER.maxWeight = 20;
        SMALL_PARCEL.maxWeight = 500;
        PARCEL.maxWeight = 1_500;
        PACKAGE.maxWeight = 10_000;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }
}
