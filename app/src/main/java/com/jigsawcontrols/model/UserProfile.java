package com.jigsawcontrols.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Krishna on 24-10-2015.
 */
public class UserProfile {

    @SerializedName("data")
    public ArrayList<Data> data;
}
