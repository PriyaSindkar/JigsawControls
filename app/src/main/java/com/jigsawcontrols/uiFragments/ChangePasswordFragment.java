package com.jigsawcontrols.uiFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jigsawcontrols.R;
import com.jigsawcontrols.apiHelpers.MyApplication;
import com.jigsawcontrols.uiActivities.HistoryActivity;
import com.jigsawcontrols.uiActivities.NewRecordActivity;
import com.jigsawcontrols.uiActivities.OldRecordActivity;
import com.jigsawcontrols.uiActivities.QuickAccessActivity;

import org.json.JSONObject;

public class ChangePasswordFragment extends Fragment {

    private EditText edtEmail, edtPassword, edtConfirmPassword;
    private Button btnLogin;
    private final String CHANGE_PASSWORD_URL = "http://jigsawserverpink.com/admin/updateProfile.php";

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment f = new ChangePasswordFragment();
        return f;
    }


    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_change_password, container, false);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        edtEmail = (EditText) view.findViewById(R.id.edtEmail);
        edtPassword = (EditText) view.findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) view.findViewById(R.id.edtConfirmPassword);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtEmail.getText().toString().trim().length() == 0) {
                    Snackbar.make(btnLogin, "Username or Email is required", Snackbar.LENGTH_LONG).show();
                } else if(edtPassword.getText().toString().trim().length() == 0) {
                    Snackbar.make(btnLogin, "Password is required", Snackbar.LENGTH_LONG).show();
                } else if(edtConfirmPassword.getText().toString().trim().length() == 0) {
                    Snackbar.make(btnLogin, "Confirm Password is required", Snackbar.LENGTH_LONG).show();
                } else if( !edtPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
                    Snackbar.make(btnLogin, "Passwords do not match", Snackbar.LENGTH_LONG).show();
                } else {
                    changePasswordPostCall();
                }

            }
        });

        return  view;
    }


    private void changePasswordPostCall() {
        try {

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("email", edtEmail.getText().toString().trim());
            jsonObject.put("pass", edtPassword.getText().toString().trim());

            Log.e("CHANGE_PASS JSON : ", "" + jsonObject.toString());


            final ProgressDialog circleDialog = ProgressDialog.show(getActivity(), "Please wait", "Loading...", true);
            circleDialog.setCancelable(true);
            circleDialog.show();

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, CHANGE_PASSWORD_URL, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject msg) {
                    circleDialog.dismiss();

                    String response1 = msg.toString();
                    Log.e("Resp CHANGE_PASS: ", "" + response1);

                    try {
                        JSONObject response = new JSONObject(msg.toString());

                        if (response.getString("status").equals("1")) {
                            Snackbar.make(btnLogin, "Password changed successfully", Snackbar.LENGTH_LONG).show();

                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = manager.beginTransaction();

                            ft.replace(R.id.frame, new HomeFragment(), "HOME");
                            ft.addToBackStack(null);
                            ft.commit();


                        } else {
                            Snackbar.make(btnLogin, "Password cannot be changed. Please try again.", Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Snackbar.make(btnLogin, "Password cannot be changed. Please try again.", Snackbar.LENGTH_LONG).show();
                        Log.e("EXCEPTION", e.toString());
                    }


                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(btnLogin, "Password cannot be changed. Please try again.", Snackbar.LENGTH_LONG).show();
                    Log.e("VOLLEY EXCEPTION", error.toString());
                    circleDialog.dismiss();


                }
            });
            MyApplication.getInstance().addToRequestQueue(req);

        } catch (Exception ex) {
            Snackbar.make(btnLogin, "Password cannot be changed. Please try again.", Snackbar.LENGTH_LONG).show();
            Log.e("JSON EXCEPTION", ex.toString());
        }
    }


// end of main class
}
