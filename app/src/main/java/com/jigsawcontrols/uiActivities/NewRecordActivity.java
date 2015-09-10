package com.jigsawcontrols.uiActivities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jigsawcontrols.R;
import com.jigsawcontrols.helpers.ComplexPreferences;
import com.jigsawcontrols.model.Order;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewRecordActivity extends AppCompatActivity {

    private Spinner spCategories, spCatSerialNo;
    private List<String> categories, catSerialNos;
    private ArrayAdapter<String> adapter;
    private TextView txtOrderDate, imgBack,txtSubmit, txtSave, txtComponent1, txtComponent2 ;
    private EditText edtComponent1, edtComponent2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_record);
        init();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Order Placed!", Snackbar.LENGTH_LONG).show();
            }
        });

        txtOrderDate.setText("Order Date: 2015-10-10 11:30:00");



        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order newOrder = new Order();
                newOrder.setOrderDate(txtOrderDate.getText().toString().trim());
                newOrder.setComponentName1(edtComponent1.getText().toString().trim());
                newOrder.setComponentName2(edtComponent2.getText().toString().trim());
                newOrder.setOrderDate(txtOrderDate.getText().toString().trim());
                newOrder.setCategory(spCategories.getSelectedItem().toString());
                newOrder.setCatSerialNumber(spCatSerialNo.getSelectedItem().toString());

                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(NewRecordActivity.this, "saved-record", 0);
                complexPreferences.putObject("saved-record", newOrder);
                complexPreferences.commit();

                Snackbar.make(view, "Order Saved For Later Use!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        categories = new ArrayList<>();

        spCategories = (Spinner) findViewById(R.id.spCategories);
        spCatSerialNo= (Spinner) findViewById(R.id.spCatSerialNo);

        txtOrderDate = (TextView) findViewById(R.id.txtOrderDate);
        txtComponent1 = (TextView) findViewById(R.id.txtComponent1);
        txtComponent2 = (TextView) findViewById(R.id.txtComponent2);
        edtComponent1 = (EditText) findViewById(R.id.edtComponent1);
        edtComponent2 = (EditText) findViewById(R.id.edtComponent2);


        imgBack = (TextView) findViewById(R.id.imgBack);

        txtSubmit = (TextView) findViewById(R.id.txtSubmit);
        txtSave = (TextView) findViewById(R.id.txtSave);

        categories.add("Category 1");
        categories.add("Category 2");

        catSerialNos = new ArrayList<>();
        catSerialNos.add("SERIAL_NO-101");
        catSerialNos.add("SERIAL_NO-102");

        adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategories.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown, catSerialNos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCatSerialNo.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // end of main class
}
