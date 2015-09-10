package com.jigsawcontrols.model;

/**
 * Created by Priya on 9/11/2015.
 */
public class Order {

    private String componentName1, componentName2,orderDate, category, catSerialNumber;

    public String getComponentName1() {
        return componentName1;
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

    public void setComponentName1(String componentName1) {
        this.componentName1 = componentName1;
    }

    public String getComponentName2() {
        return componentName2;
    }

    public void setComponentName2(String componentName2) {
        this.componentName2 = componentName2;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
