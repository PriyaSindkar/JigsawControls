package com.jigsawcontrols.model;

import java.util.ArrayList;

/**
 * Created by Priya on 9/11/2015.
 */
public class Order {

    private String orderDate, category, catSerialNumber;
    private ArrayList<Component> components;

    public ArrayList<Component> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<Component> components) {
        this.components = components;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCatSerialNumber() {
        return catSerialNumber;
    }

    public void setCatSerialNumber(String catSerialNumber) {
        this.catSerialNumber = catSerialNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


    @Override
    public String toString() {
        return "Order{" +
                "orderDate='" + orderDate + '\'' +
                ", category='" + category + '\'' +
                ", catSerialNumber='" + catSerialNumber + '\'' +
                ", components=" + components +
                '}';
    }
}
