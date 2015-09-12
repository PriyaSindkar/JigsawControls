package com.jigsawcontrols.uiActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jigsawcontrols.R;
import com.jigsawcontrols.helpers.ComplexPreferences;
import com.jigsawcontrols.helpers.Utility;
import com.jigsawcontrols.model.Component;
import com.jigsawcontrols.model.Order;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewRecordActivity extends AppCompatActivity{

    private Spinner spCategories, spCatSerialNo;
    private List<String> categories, catSerialNos;
    private ArrayAdapter<String> adapter;
    private TextView txtOrderDate, imgBack,txtSubmit, txtSave;
    private ImageView imgComponent1, imgCapture1, image;
    private Bitmap thumbnail;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private LinearLayout linearComponentsParent;
    private Order newOrder;
    private int noOfComponents = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_record);
        init();

        addImageLayout(noOfComponents);

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


                if(spCategories.getSelectedItem().toString().trim().equals("Select Trolley Category")) {
                    Snackbar snack = Snackbar.make(spCategories, "Please Select a Category!", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
                else if(spCatSerialNo.getSelectedItem().toString().trim().equals("Select Serial Number")) {
                    Snackbar snack = Snackbar.make(spCatSerialNo, "Please Select Serial No!", Snackbar.LENGTH_SHORT);
                    snack.show();
                } else {
                    newOrder = new Order();
                    newOrder.setOrderDate(txtOrderDate.getText().toString().trim());
                    newOrder.setCategory(spCategories.getSelectedItem().toString());
                    newOrder.setCatSerialNumber(spCatSerialNo.getSelectedItem().toString());
                    newOrder.setComponents(getComponentsDetails());

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(NewRecordActivity.this, "saved-record", 0);
                    complexPreferences.putObject("saved-record", newOrder);
                    complexPreferences.commit();

                    Snackbar.make(view, "Order Saved For Later Use!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        categories = new ArrayList<>();

        spCategories = (Spinner) findViewById(R.id.spCategories);
        spCatSerialNo= (Spinner) findViewById(R.id.spCatSerialNo);

        txtOrderDate = (TextView) findViewById(R.id.txtOrderDate);
        linearComponentsParent = (LinearLayout) findViewById(R.id.linearComponentsParent);


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

    private void addImageLayout(int noOfComponents) {
        for(int i=0; i<noOfComponents; i++) {
            LayoutInflater inflater = LayoutInflater.from(NewRecordActivity.this);
            View inflatedLayout = inflater.inflate(R.layout.component_layout, null, false);
            linearComponentsParent.addView(inflatedLayout);

            TextView txtComponent1 = (TextView) inflatedLayout.findViewById(R.id.txtComponent1);
            txtComponent1.setText("Component "+ i);
            imgCapture1 = (ImageView) inflatedLayout.findViewById(R.id.imgCapture1);
            imgComponent1 = (ImageView) inflatedLayout.findViewById(R.id.imgComponent1);
            //image = imgComponent1;

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

    private ArrayList<Component> getComponentsDetails() {
        ArrayList<Component> components = new ArrayList<>();
       // int noOfComponets = linearComponentsParent.getChildCount();

        for(int i=0; i<noOfComponents; i++) {
            View child = linearComponentsParent.getChildAt(i);
            String componentName = ((TextView) child.findViewById(R.id.txtComponent1)).getText().toString().trim();
            String componentDetails = ((EditText) child.findViewById(R.id.edtComponent1)).getText().toString().trim();
            ImageView componentImage = (ImageView) child.findViewById(R.id.imgComponent1);
            String componentPhoto = "";
            if(componentImage.getDrawable()!=null) {
                componentPhoto = Utility.returnBas64Image(getImage(componentImage));
            }
            Component component = new Component(componentName, componentDetails, componentPhoto);
            components.add(component);

        }
        return components;
    }

    public Bitmap getImage(ImageView ivImage) {
        return ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
    }
    // end of main class
}
