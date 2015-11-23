package com.jigsawcontrols.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


/**
 * Created by xitij on 17-03-2015.
 */
public class PrefUtils {

    public static void saveTime(Context ctx,String City){
        Prefs.with(ctx).save("City",City);
    }

    public static String returnCity(Context ctx){
        String val = Prefs.with(ctx).getString("City","");
        return val;
    }








}
