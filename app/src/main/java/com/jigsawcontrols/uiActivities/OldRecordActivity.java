package com.jigsawcontrols.uiActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jigsawcontrols.R;
import com.jigsawcontrols.helpers.ComplexPreferences;
import com.jigsawcontrols.model.Component;
import com.jigsawcontrols.model.Order;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OldRecordActivity extends AppCompatActivity {

    private TextView txtOrderDate, imgBack,txtSubmit ;
    private Spinner spCategories, spCatSerialNo;
    private List<String> categories, catSerialNos;
    private ArrayAdapter<String> adapter;
    private Order savedOrder;
    private ImageView imgComponent1, imgCapture1, image;
    private Bitmap thumbnail;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private LinearLayout linearComponentsParent;
    private Order newOrder;
    private int noOfComponents = 2;
    ComplexPreferences complexPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_record);

        complexPreferences = ComplexPreferences.getComplexPreferences(OldRecordActivity.this, "saved-record", 0);
        savedOrder = complexPreferences.getObject("saved-record", Order.class);


        if(savedOrder != null) {
            init();
            setSavedDetails();
        } else {
            setContentView(R.layout.empty_layout);
            imgBack = (TextView) findViewById(R.id.imgBack);
            imgBack.setText("Work In Progress");

            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }



    }

    private void init() {

        txtOrderDate = (TextView) findViewById(R.id.txtOrderDate);

        txtSubmit = (TextView) findViewById(R.id.txtSubmit);
        TextView txtSave = (TextView) findViewById(R.id.txtSave);
        txtSave.setVisibility(View.GONE);
        View line = findViewById(R.id.line);
        line.setVisibility(View.GONE);

        imgBack = (TextView) findViewById(R.id.imgBack);
        imgBack.setText("Work In Progress");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        linearComponentsParent = (LinearLayout) findViewById(R.id.linearComponentsParent);

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



        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complexPreferences.clearObject();
                complexPreferences.commit();
                Snackbar.make(view, "Order Placed!", Snackbar.LENGTH_LONG).show();
            }
        });

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

    private void setSavedDetails() {

        if(savedOrder!=null) {
            spCategories.setSelection(getSimpleIndex(spCategories, savedOrder.getCategory()));
            spCatSerialNo.setSelection(getSimpleIndex(spCatSerialNo, savedOrder.getCatSerialNumber()));
            txtOrderDate.setText(savedOrder.getOrderDate());
            addImageLayout();
        }


    }

    private void addImageLayout() {
            ArrayList<Component> savedOrderComponents = savedOrder.getComponents();
            for(int i=0; i<savedOrderComponents.size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(OldRecordActivity.this);
                View inflatedLayout = inflater.inflate(R.layout.component_layout, null, false);
                linearComponentsParent.addView(inflatedLayout);

                TextView txtComponent1 = (TextView) inflatedLayout.findViewById(R.id.txtComponent1);
                txtComponent1.setText(savedOrderComponents.get(i).getComponentName());

                EditText edtComponent1 = (EditText) inflatedLayout.findViewById(R.id.edtComponent1);
                edtComponent1.setText(savedOrderComponents.get(i).getComponentDetails());

                imgCapture1 = (ImageView) inflatedLayout.findViewById(R.id.imgCapture1);
                imgComponent1 = (ImageView) inflatedLayout.findViewById(R.id.imgComponent1);
                imgComponent1.setImageBitmap(getImageBitmapFromString(savedOrderComponents.get(i).getComponentPhoto()));

                imgCapture1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        image = (ImageView) ((LinearLayout) view.getParent()).getChildAt(0);
                        captureImage();
                        //image.setImageBitmap(thumbnail);
                    }
                });
            }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            } else if (resultCode == RESULT_CANCELED) {
                Snackbar snack = Snackbar.make(imgCapture1, "User cancelled image capture", Snackbar.LENGTH_LONG);
                snack.show();
            } else {
                Snackbar snack = Snackbar.make(imgCapture1, "Sorry! Failed to capture image", Snackbar.LENGTH_LONG);
                snack.show();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        image.setImageBitmap(thumbnail);
    }

    public Bitmap getImageBitmapFromString(String profilePic) {
        byte [] encodeByte= Base64.decode(profilePic, Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        return bitmap;
    }

    public Bitmap getImage(ImageView ivImage) {
        return ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
    }


    // end of main class
}
