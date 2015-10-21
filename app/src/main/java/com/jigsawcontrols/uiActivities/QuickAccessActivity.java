package com.jigsawcontrols.uiActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jigsawcontrols.R;
import com.jigsawcontrols.helpers.AdvancedSpannableString;
import com.jungly.gridpasswordview.GridPasswordView;

import org.w3c.dom.Text;

public class QuickAccessActivity extends AppCompatActivity {
    private Button btnLogin;
    private GridPasswordView pswView;
    private TextView txtForgotAccessCode, txtAccessCodeChanged,txtBottomCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_acess);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        pswView = (GridPasswordView) findViewById(R.id.pswView);
        txtForgotAccessCode = (TextView) findViewById(R.id.txtForgotAccessCode);
        txtAccessCodeChanged = (TextView) findViewById(R.id.txtAccessCodeChanged);
        txtBottomCode= (TextView) findViewById(R.id.txtBottomCode);

        AdvancedSpannableString span = new AdvancedSpannableString("Your Quick Access Code is 123456 by default.");
        span.setBold("123456");
        txtBottomCode.setText(span);

        if(getIntent().getBooleanExtra("is_access_code_default", false)) {
            txtAccessCodeChanged.setVisibility(View.VISIBLE);
        } else {
            txtAccessCodeChanged.setVisibility(View.GONE);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pswView.getPassWord().length() != 6) {
                    Snackbar.make(view, "Access Code must be 6 digits.", Snackbar.LENGTH_LONG).show();
                } else {

                    SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                    if (getIntent().getBooleanExtra("is_access_code_default", false)) {
                        if ("123456".equals(pswView.getPassWord())) {
                            if (preferences.contains("isUserLogin")) {
                                Intent intent = new Intent(getBaseContext(), MyDrawerActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(QuickAccessActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Snackbar.make(view, "Please enter valid access code.", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        if (preferences.getString("quick_access_code", "").equals(pswView.getPassWord())) {
                            if (preferences.contains("isUserLogin")) {
                                Intent intent = new Intent(getBaseContext(), MyDrawerActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(QuickAccessActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            if (preferences.getBoolean("isFirstTimeAccess", false)) {
                                preferences.edit().putBoolean("isFirstTimeAccess", false).commit();
                                preferences.edit().putString("quick_access_code", "123456").commit();

                                Intent intent = new Intent(QuickAccessActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Snackbar.make(view, "Please enter valid access code.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });

        txtForgotAccessCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuickAccessActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("is_forgot_access_code", true);
                startActivity(intent);
                finish();
            }
        });
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
