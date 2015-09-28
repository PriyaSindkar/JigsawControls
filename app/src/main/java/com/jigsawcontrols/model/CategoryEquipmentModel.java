package com.jigsawcontrols.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Priya on 9/27/2015.
 */
public class CategoryEquipmentModel {

    @SerializedName("cat_id")
    public String categoryId;

    @SerializedName("cat_name")
    public String categoryName;

    @SerializedName("t_id")
    public String equipmentId;

    @SerializedName("name")
    public String equipmentName;
}
