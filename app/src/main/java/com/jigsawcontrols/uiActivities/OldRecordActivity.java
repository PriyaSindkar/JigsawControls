package com.jigsawcontrols.uiActivities;

import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jigsawcontrols.R;
import com.jigsawcontrols.adapters.CustomSpinnerAdapter;
import com.jigsawcontrols.apiHelpers.CallWebService;
import com.jigsawcontrols.helpers.ComplexPreferences;
import com.jigsawcontrols.model.CategoryEquipmentModel;
import com.jigsawcontrols.model.Component;
import com.jigsawcontrols.model.Order;
import com.jigsawcontrols.model.TemplateModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
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

    private String GET_TEMPLATES_URL = "http://jigsawserverpink.com/admin/getTemplate.php";
    private ArrayList<CategoryEquipmentModel> categoryEquipmentModels;
    private ArrayList<Object> categoriesList = new ArrayList<>();

    ArrayList<Component> equipment;
    private CustomSpinnerAdapter mAdapter;
    private ArrayList<Component> componentsForTemplateSelected;
    private boolean isFirstTime = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_record);

        complexPreferences = ComplexPreferences.getComplexPreferences(OldRecordActivity.this, "saved-record", 0);
        savedOrder = complexPreferences.getObject("saved-record", Order.class);


        if(savedOrder != null) {
            init();

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

        getTemplates();

        catSerialNos = new ArrayList<>();
        catSerialNos.add("JSW20091517-01");
        catSerialNos.add("JSW20091517-02");
        catSerialNos.add("JSW20091517-03");

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

        /*spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if( !isFirstTime) {
                    if (spCategories.getItemAtPosition(i) != null) {
                        String templateId = ((TemplateModel) mAdapter.getItem(i)).getTemplateId();
                        componentsForTemplateSelected = new ArrayList<Component>();
                        for (int c = 0; c < equipment.size(); c++) {
                            if (equipment.get(c).getCategoryId().equals(templateId)) {
                                componentsForTemplateSelected.add(equipment.get(c));
                            }
                        }
                        addImageLayout(componentsForTemplateSelected);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

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

    private int getTemplateIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i=1;i<spinner.getCount(); i++){
            TemplateModel templateModel = (TemplateModel) spinner.getItemAtPosition(i);
            if ( templateModel.getTemplateName().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    private void setSavedDetails() {

        if(savedOrder!=null) {
            spCategories.setSelection(getTemplateIndex(spCategories, savedOrder.getCategory()));
            spCatSerialNo.setSelection(getSimpleIndex(spCatSerialNo, savedOrder.getCatSerialNumber()));
            txtOrderDate.setText(savedOrder.getOrderDate());
            addImageLayout(savedOrder.getComponents());
        }
    }

    private void addImageLayout(ArrayList<Component> savedOrderComponents) {
            linearComponentsParent.removeAllViews();
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
                if(savedOrderComponents.get(i).getComponentPhoto() != null && !savedOrderComponents.get(i).getComponentPhoto().equals("")) {
                    imgComponent1.setImageBitmap(getImageBitmapFromString(savedOrderComponents.get(i).getComponentPhoto()));
                }

                imgCapture1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        image = (ImageView) ((LinearLayout) view.getParent()).getChildAt(0);
                        captureImage();
                    }
                });
            }
    }


    private void getTemplates() {

        final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
        circleDialog.setCancelable(true);
        circleDialog.show();

        new CallWebService(GET_TEMPLATES_URL, CallWebService.TYPE_JSONOBJECT) {

            @Override
            public void response(String response) {

                circleDialog.dismiss();

                Log.e("RESP templates_Details", response);

                try {
                    JSONObject msg = new JSONObject(response);
                    if (msg.getString("status").equals("1")) {
                        JSONObject infoJSONObj= msg.getJSONObject("info");

                        Iterator<String> iterator = infoJSONObj.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            Type listType = new TypeToken<List<CategoryEquipmentModel>>() {
                            }.getType();

                            categoryEquipmentModels =  new GsonBuilder().create().fromJson(infoJSONObj.optString(key).toString(), listType);
                            categoriesList.add(categoryEquipmentModels);

                        }
                        setCategories();
                        setSavedDetails();
                    }
                }catch(JSONException jsonEx) {
                    Log.e("JSON EXCEPTION: ", jsonEx.toString());
                }
            }

            @Override
            public void error(VolleyError error) {
                Log.e("VOLLEY ERROR", error.toString());
                circleDialog.dismiss();
            }
        }.start();
    }

    private void setCategories() {
        ArrayList<TemplateModel> templates = new ArrayList<>();
        categories.clear();
        equipment = new ArrayList<>();
        for(int i=0; i<categoriesList.size(); i++) {
            ArrayList<CategoryEquipmentModel> list = (ArrayList<CategoryEquipmentModel>) categoriesList.get(i);
            TemplateModel template =  new TemplateModel(list.get(0).categoryId, list.get(0).categoryName);
            templates.add(template);

            categories.add(list.get(0).categoryName);
            for(int j=0; j<list.size(); j++) {
                Component component = new Component( );
                component.setCategoryId(list.get(j).categoryId);
                component.setComponentName(list.get(j).equipmentName);
                component.setComponentDetails("");
                equipment.add(component);
            }
        }
        mAdapter = new CustomSpinnerAdapter(OldRecordActivity.this, templates,R.layout.spinner_dropdown, R.layout.spinner_layout );
        spCategories.setAdapter(mAdapter);

        Log.e("equipment", equipment.toString());
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
            byte[] encodeByte = Base64.decode(profilePic, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        return bitmap;
    }

    public Bitmap getImage(ImageView ivImage) {
        return ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
    }


    // end of main class
}
