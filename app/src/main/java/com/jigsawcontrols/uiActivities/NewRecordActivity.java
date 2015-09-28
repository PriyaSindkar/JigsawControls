package com.jigsawcontrols.uiActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.jigsawcontrols.helpers.Utility;
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

public class NewRecordActivity extends AppCompatActivity{

    private Spinner spCategories, spCatSerialNo;
    private List<String> categories, catSerialNos;
    private ArrayAdapter<String> categoriesAdapter;
    private ArrayAdapter<String> serialNoAdapter;
    private TextView txtOrderDate, imgBack,txtSubmit, txtSave;
    private ImageView imgComponent1, imgCapture1, image;
    private Bitmap thumbnail;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private LinearLayout linearComponentsParent;
    private Order newOrder;

    private String GET_TEMPLATES_URL = "http://jigsawserverpink.com/admin/getTemplate.php";
    private ArrayList<CategoryEquipmentModel> categoryEquipmentModels;
    private ArrayList<Object> categoriesList = new ArrayList<>();

    ArrayList<Component> equipment;
    private CustomSpinnerAdapter mAdapter;
    private ArrayList<Component> componentsForTemplateSelected;

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


                if (spCategories.getSelectedItem().toString().trim().equals("Select Trolley Category")) {
                    Snackbar snack = Snackbar.make(spCategories, "Please Select a Category!", Snackbar.LENGTH_SHORT);
                    snack.show();
                } else if (spCatSerialNo.getSelectedItem().toString().trim().equals("Select Serial Number")) {
                    Snackbar snack = Snackbar.make(spCatSerialNo, "Please Select Serial No!", Snackbar.LENGTH_SHORT);
                    snack.show();
                } else {
                    newOrder = new Order();
                    newOrder.setOrderDate(txtOrderDate.getText().toString().trim());
                    Log.e("CAT_SELECTED", ((TemplateModel) spCategories.getSelectedItem()).getTemplateName());
                    newOrder.setCategory(((TemplateModel)spCategories.getSelectedItem()).getTemplateName() );
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

        getTemplates();

        catSerialNos = new ArrayList<>();
        catSerialNos.add("JSW20091517-01");
        catSerialNos.add("JSW20091517-02");
        catSerialNos.add("JSW20091517-03");

        serialNoAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown, catSerialNos);
        serialNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCatSerialNo.setAdapter(serialNoAdapter);

        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spCategories.getItemAtPosition(i) != null) {
                    String templateId = ((TemplateModel) mAdapter.getItem(i)).getTemplateId();
                    componentsForTemplateSelected = new ArrayList<Component>();
                    for(int c=0; c < equipment.size();c++) {
                        if(equipment.get(c).getCategoryId().equals(templateId)) {
                            componentsForTemplateSelected.add(equipment.get(c));
                        }
                    }

                    addImageLayout(componentsForTemplateSelected);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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

    private void addImageLayout(ArrayList<Component> components) {

        linearComponentsParent.removeAllViews();

        for(int i=0; i<components.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(NewRecordActivity.this);
            View inflatedLayout = inflater.inflate(R.layout.component_layout, null, false);
            linearComponentsParent.addView(inflatedLayout);

            TextView txtComponent1 = (TextView) inflatedLayout.findViewById(R.id.txtComponent1);
            txtComponent1.setText(components.get(i).getComponentName());
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

    /*private void addImageLayout(int noOfComponents) {
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
    }*/

    private ArrayList<Component> getComponentsDetails() {
        ArrayList<Component> components = new ArrayList<>();
       // int noOfComponets = linearComponentsParent.getChildCount();
        if(componentsForTemplateSelected != null) {
            for(int i=0; i<componentsForTemplateSelected.size(); i++) {
                View child = linearComponentsParent.getChildAt(i);
                String componentName = ((TextView) child.findViewById(R.id.txtComponent1)).getText().toString().trim();
                String componentDetails = ((EditText) child.findViewById(R.id.edtComponent1)).getText().toString().trim();
                ImageView componentImage = (ImageView) child.findViewById(R.id.imgComponent1);
                String componentPhoto = "";
                if (componentImage.getDrawable() != null) {
                    componentPhoto = Utility.returnBas64Image(getImage(componentImage));
                }
                Component component = new Component(componentName, componentDetails, componentPhoto);
                components.add(component);
            }
        }
        return components;
    }

    public Bitmap getImage(ImageView ivImage) {
        return ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
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
                    //JSONArray info = msg.getJSONArray("info");

                    if (msg.getString("status").equals("1")) {
                        //Log.e("info", info.toString());

                      //  JSONObject mainJSONObject = new JSONObject(info.toString());
                        // get category JSONObject from mainJSONObj
                        JSONObject infoJSONObj= msg.getJSONObject("info");

                        // get all keys from categoryJSONObj

                        Iterator<String> iterator = infoJSONObj.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            //Log.e("TAG", "key:" + key + "--Value::" + infoJSONObj.optString(key));
                            Type listType = new TypeToken<List<CategoryEquipmentModel>>() {
                            }.getType();

                            categoryEquipmentModels =  new GsonBuilder().create().fromJson(infoJSONObj.optString(key).toString(), listType);
                            Log.e("categoryEquipmentModels", categoryEquipmentModels.toString());
                            categoriesList.add(categoryEquipmentModels);

                        }
                        Log.e("Final list", categoriesList.toString());
                        setCategories();

                        /*Type listType = new TypeToken<List<CategoryModel>>() {
                        }.getType();

                        categoryItems = new GsonBuilder().create().fromJson(data.toString(), listType);
                        //filterHomePageCouponList(couponList);


*/
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
        mAdapter = new CustomSpinnerAdapter(NewRecordActivity.this, templates,R.layout.spinner_dropdown, R.layout.spinner_layout );
        spCategories.setAdapter(mAdapter);

        Log.e("equipment", equipment.toString());
    }

    // end of main class
}
