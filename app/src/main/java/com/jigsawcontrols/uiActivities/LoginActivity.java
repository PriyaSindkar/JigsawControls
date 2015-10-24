package com.jigsawcontrols.uiActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jigsawcontrols.R;
import com.jigsawcontrols.apiHelpers.MyApplication;
import com.jigsawcontrols.uiFragments.HomeFragment;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText edtUsername, edtPassword;
    private final String LOGIN_URL = "http://jigsawserverpink.com/admin/getLogin.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        if(getIntent().getBooleanExtra("is_forgot_access_code",false) ) {
            Snackbar.make(btnLogin, "Please provide login details to get new access code.", Snackbar.LENGTH_LONG).show();
            btnLogin.setText("GET NEW ACCESS CODE");
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtUsername.getText().toString().trim().length() == 0) {
                    Snackbar.make(btnLogin, "Username or Email is required", Snackbar.LENGTH_LONG).show();
                } else if(edtPassword.getText().toString().trim().length() == 0) {
                    Snackbar.make(btnLogin, "Password is required", Snackbar.LENGTH_LONG).show();
                } else {
                    loginPostCall();
                }
            }
        });
    }

    private void loginPostCall() {
        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("email", edtUsername.getText().toString().trim());
            jsonObject.put("pass", edtPassword.getText().toString().trim());

            Log.e("LOGIN JSON : ", "" + jsonObject.toString());


            final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
            circleDialog.setCancelable(true);
            circleDialog.show();

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject msg) {
                    circleDialog.dismiss();



                    try {
                        JSONObject response = new JSONObject(msg.toString());
                        JSONArray data = response.getJSONArray("data");
                        Log.e("Resp LOGIN: ", "" + response);
                        Log.e("Resp DATA: ", "" + data);

                       // if ( msg.getString("data")!= null &&  msg.getString("data").equals("0")) {
                            SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isUserLogin", true);
                            editor.putBoolean("isFirstTimeLogin", true);
                            editor.commit();

                            if(getIntent().getBooleanExtra("is_forgot_access_code",false) ) {
                                preferences.edit().putString("quick_access_code", "123456").commit();

                                Intent intent = new Intent(LoginActivity.this, QuickAccessActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("is_access_code_default", true);
                                startActivity(intent);
                                finish();

                            } else {
                                Intent intent = new Intent(LoginActivity.this, MyDrawerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        /*} else {
                            Snackbar.make(btnLogin, "Username and password incorrect. Please try again.", Snackbar.LENGTH_LONG).show();
                        }*/
                    } catch (Exception e) {
                        Snackbar.make(btnLogin, "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
                        Log.e("EXCEPTION", e.toString());
                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(btnLogin, "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
                    Log.e("VOLLEY EXCEPTION", error.toString());
                    circleDialog.dismiss();


                }
            });
            MyApplication.getInstance().addToRequestQueue(req);

        } catch (Exception ex) {
            Snackbar.make(btnLogin, "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
