package com.jigsawcontrols.apiHelpers;

/**
 * Created by Priya on 9/15/2015.
 */
import com.android.volley.VolleyError;

public interface IService {

    public void response(String response);
    public void error(VolleyError error);

}
