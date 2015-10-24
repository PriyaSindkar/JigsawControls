package com.jigsawcontrols.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Priya on 9/27/2015.
 */
public class OrderHistoryModel {

    @SerializedName("o_id")
    public String orderId;

    @SerializedName("o_date")
    public String orderDate;

    @SerializedName("trolley_catg_name")
    public String trolleyCategoryName;

    @SerializedName("serial_no")
    public String serialNo;

    @SerializedName("equipment_details")
    public String equipmentDetails;

    @SerializedName("equipment_images")
    public String images;

    @SerializedName("adminName")
    public String adminName;

    @SerializedName("adminEmail")
    public String adminEmail;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTrolleyCategoryName() {
        return trolleyCategoryName;
    }

    public void setTrolleyCategoryName(String trolleyCategoryName) {
        this.trolleyCategoryName = trolleyCategoryName;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getEquipmentDetails() {
        return equipmentDetails;
    }

    public void setEquipmentDetails(String equipmentDetails) {
        this.equipmentDetails = equipmentDetails;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}
