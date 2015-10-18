package com.jigsawcontrols.uiFragments;

import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jigsawcontrols.R;
import com.jigsawcontrols.apiHelpers.MyApplication;

import org.json.JSONObject;

public class ChangeAccessCodeFragment extends Fragment {

    private EditText edtAccessCode;
    private Button btnLogin;

    public static ChangeAccessCodeFragment newInstance() {
        ChangeAccessCodeFragment f = new ChangeAccessCodeFragment();
        return f;
    }


    public ChangeAccessCodeFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_change_access_code, container, false);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        edtAccessCode = (EditText) view.findViewById(R.id.edtAccessCode);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtAccessCode.getText().toString().trim().length() == 0) {
                    Snackbar.make(btnLogin, "Please enter new access code", Snackbar.LENGTH_LONG).show();
                } else {
                    SharedPreferences preferences = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
                    preferences.edit().putString("quick_access_code", edtAccessCode.getText().toString().trim()).commit();

                    Snackbar.make(view, "Access Code changed successfully.", Snackbar.LENGTH_LONG).show();

                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();

                    ft.replace(R.id.frame, new HomeFragment(), "HOME");
                    ft.addToBackStack(null);
                    ft.commit();

                }

            }
        });

        return  view;
    }

// end of main class
}
