package com.jigsawcontrols.apiHelpers;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Application class that called once when application is installed for the first time on device.
 * This class includes the integration of Volly [third party framework for calling webservices]
 */
public class MyApplication extends Application {

    /**
     * Log or request TAG
     */
    public static final String TAG = "VolleyPatterns";

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    /**
     * A singleton instance of the application class for easy access in other places
     */
    private static MyApplication sInstance;
    /**
     * A class that helps to store database file from assets to
     */
    static int halfscreenSize;
    @Override
    public void onCreate() {
        super.onCreate();
        // initialize the singleton
        sInstance = this;

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        if(preferences.contains("isFirstTimeAccess")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTimeAccess", false);
            editor.commit();
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTimeAccess", true);
            editor.commit();
        }


        Log.e("app", "app");

    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized MyApplication getInstance() {
        return sInstance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);

    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     * @param
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelAll() {
        try {
            mRequestQueue.cancelAll(null);
        } catch (Exception e) {

        }
    }
}
