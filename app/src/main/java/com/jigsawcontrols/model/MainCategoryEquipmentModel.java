package com.jigsawcontrols.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Priya on 9/27/2015.
 */
public class MainCategoryEquipmentModel {

    public ArrayList<CategoryEquipmentModel> categoryEquipmentModels;

    public ArrayList<CategoryEquipmentModel> getCategoryEquipmentModels() {
        return categoryEquipmentModels;
    }

    public void add(CategoryEquipmentModel categoryEquipmentModel) {
        this.categoryEquipmentModels.add(categoryEquipmentModel);
    }
}
