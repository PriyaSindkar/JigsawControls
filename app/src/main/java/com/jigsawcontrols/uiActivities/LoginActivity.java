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
import com.google.gson.GsonBuilder;
import com.jigsawcontrols.R;
import com.jigsawcontrols.apiHelpers.EnumType;
import com.jigsawcontrols.apiHelpers.GetPostClass;
import com.jigsawcontrols.apiHelpers.MyApplication;
import com.jigsawcontrols.helpers.ComplexPreferences;
import com.jigsawcontrols.model.UserProfile;
import com.jigsawcontrols.uiFragments.HomeFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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


        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("email", edtUsername.getText().toString().trim()));
        pairs.add(new BasicNameValuePair("pass", edtPassword.getText().toString().trim()));

        final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
        circleDialog.setCancelable(true);
        circleDialog.show();

        new GetPostClass(LOGIN_URL, pairs, EnumType.POST) {

            @Override
            public void response(String msg) {
                circleDialog.dismiss();
                try {
                    JSONObject response = new JSONObject(msg.toString());
                    JSONArray data = response.getJSONArray("data");
                    Log.e("Resp LOGIN: ", "" + response);
                    Log.e("Resp DATA: ", "" + data);


                    if (data.length()!=0) {
                        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isUserLogin", true);
                        editor.putBoolean("isFirstTimeLogin", true);
                        editor.commit();

                        if (getIntent().getBooleanExtra("is_forgot_access_code", false)) {
                            preferences.edit().putString("quick_access_code", "123456").commit();

                            Intent intent = new Intent(LoginActivity.this, QuickAccessActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("is_access_code_default", true);
                            startActivity(intent);
                            finish();

                        } else {

                            UserProfile profile = new GsonBuilder().create().fromJson(msg, UserProfile.class);
                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "user_pref", 0);
                            complexPreferences.putObject("current-user", profile);
                            complexPreferences.commit();

                            if(profile.data.get(0).isactive.equalsIgnoreCase("1")) {
                                Intent intent = new Intent(LoginActivity.this, MyDrawerActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                Snackbar.make(btnLogin, "Administrator has blocked you. \nPlease contact at info@jigsawcontrols.com", Snackbar.LENGTH_LONG).show();
                            }
                        }

                    }else{
                        Snackbar.make(btnLogin, "Invalid email or password. Please try again.", Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Snackbar.make(btnLogin, "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
                    Log.e("EXCEPTION", e.toString());
                }


            }

            @Override
            public void error(String error) {
                Snackbar.make(btnLogin, "Login failed. Please try again.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
                circleDialog.dismiss();
            }
        }.call();
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
