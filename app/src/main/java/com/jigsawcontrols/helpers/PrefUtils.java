package com.jigsawcontrols.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class PrefUtils {


    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static void saveTime(Context ctx,String time){
        Prefs.with(ctx).save("time", time);
    }

    public static String returnTime(Context ctx){
        String val = Prefs.with(ctx).getString("time","");
        return val;
    }


    public static boolean isTimeMoreThan1Hour(String current,String old){
        boolean isTimeMore1Hour = false;

        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

            DateTime startTime = formatter.parseDateTime(current);
            DateTime endTime = formatter.parseDateTime(old);

            Period p = new Period(startTime, endTime);
            int hours = p.getHours();
            if(hours!=0){
                isTimeMore1Hour = true;
            }

        }catch (Exception e){
            Log.e("### Exc date conv",e.toString());
        }

        return isTimeMore1Hour;
    }







}
