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
import java.util.List;

public class OldRecordActivity extends AppCompatActivity {

    private TextView txtOrderDate, imgBack,txtSubmit, txtComponent1, txtComponent2 ;
    private Spinner spCategories, spCatSerialNo;
    private List<String> categories, catSerialNos;
    private ArrayAdapter<String> adapter;
    private EditText edtComponent1, edtComponent2;
    private Order savedOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_old_record);

        final ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(OldRecordActivity.this, "saved-record", 0);
        savedOrder = complexPreferences.getObject("saved-record", Order.class);

        init();
        setSavedDetails();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complexPreferences.clearObject();
                complexPreferences.commit();
                Snackbar.make(view, "Order Placed!", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    private void setSavedDetails() {

        if(savedOrder!=null) {
            spCategories.setSelection(getSimpleIndex(spCategories, savedOrder.getCategory()));
            spCatSerialNo.setSelection(getSimpleIndex(spCatSerialNo, savedOrder.getCatSerialNumber()));
            edtComponent1.setText(savedOrder.getComponentName1());
            edtComponent2.setText(savedOrder.getComponentName2());
            txtOrderDate.setText(savedOrder.getOrderDate());
        }


    }

    private void init() {

        txtOrderDate = (TextView) findViewById(R.id.txtOrderDate);
        txtComponent1 = (TextView) findViewById(R.id.txtComponent1);
        txtComponent2 = (TextView) findViewById(R.id.txtComponent2);
        imgBack = (TextView) findViewById(R.id.imgBack);
        edtComponent1 = (EditText) findViewById(R.id.edtComponent1);
        edtComponent2 = (EditText) findViewById(R.id.edtComponent2);

        txtSubmit = (TextView) findViewById(R.id.txtSubmit);

        categories = new ArrayList<>();

        spCategories = (Spinner) findViewById(R.id.spCategories);
        spCatSerialNo= (Spinner) findViewById(R.id.spCatSerialNo);

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

    private int getSimpleIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i=0;i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }


    // end of main class
}
