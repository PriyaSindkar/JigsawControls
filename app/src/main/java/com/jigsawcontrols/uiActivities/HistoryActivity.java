package com.jigsawcontrols.uiActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jigsawcontrols.R;
import com.jigsawcontrols.apiHelpers.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends ActionBarActivity {
    private Spinner spCatSerialNo;
    private List<String> catSerialNos;
    private ArrayAdapter<String> adapter;
    private final String HISTORY_POST_URL = "http://jigsawserverpink.com/admin/getHistory.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_history);
        spCatSerialNo= (Spinner) findViewById(R.id.spCatSerialNo);

        catSerialNos = new ArrayList<>();
        catSerialNos.add("SERIAL_NO-101");
        catSerialNos.add("SERIAL_NO-102");

        adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown, catSerialNos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCatSerialNo.setAdapter(adapter);

        getHistory();
    }


    private void getHistory() {
        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("email", "test@gmail.com");

            Log.e("HISTORY JSON : ", "" + jsonObject.toString());


            final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
            circleDialog.setCancelable(true);
            circleDialog.show();

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, HISTORY_POST_URL, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject msg) {
                    circleDialog.dismiss();


                    try {
                        JSONObject response = new JSONObject(msg.toString());
                        JSONArray data = response.getJSONArray("data");
                        Log.e("Resp HISTORY: ", "" + response);

                        if (msg.getString("data") != null && msg.getString("data").equals("0")) {


                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Username and password incorrect. Please try again.", Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        // Snackbar.make(btnLogin, "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
                        Log.e("EXCEPTION", e.toString());
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(findViewById(android.R.id.content), "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
                    Log.e("VOLLEY EXCEPTION", error.toString());
                    circleDialog.dismiss();
                }
            });
            MyApplication.getInstance().addToRequestQueue(req);

        } catch (Exception ex) {
            Snackbar.make(findViewById(android.R.id.content), "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
            Log.e("JSON EXCEPTION", ex.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
