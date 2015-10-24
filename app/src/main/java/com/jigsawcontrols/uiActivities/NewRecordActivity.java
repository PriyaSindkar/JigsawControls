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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jigsawcontrols.R;
import com.jigsawcontrols.adapters.CustomSerialNoSpinnerAdapter;
import com.jigsawcontrols.adapters.CustomTemplateSpinnerAdapter;
import com.jigsawcontrols.apiHelpers.CallWebService;
import com.jigsawcontrols.apiHelpers.EnumType;
import com.jigsawcontrols.apiHelpers.GetPostClass;
import com.jigsawcontrols.apiHelpers.MyApplication;
import com.jigsawcontrols.helpers.ComplexPreferences;
import com.jigsawcontrols.helpers.Utility;
import com.jigsawcontrols.model.CategoryEquipmentModel;
import com.jigsawcontrols.model.Component;
import com.jigsawcontrols.model.Order;
import com.jigsawcontrols.model.SerialNoModel;
import com.jigsawcontrols.model.TemplateModel;
import com.jigsawcontrols.model.UserProfile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class NewRecordActivity extends AppCompatActivity {

    private Spinner spCategories, spCatSerialNo;
    private List<String> categories, catSerialNos;
    private ArrayAdapter<String> categoriesAdapter;
    private ArrayAdapter<String> serialNoAdapter;
    private TextView txtOrderDate, imgBack, txtSubmit, txtSave;
    private ImageView imgComponent1, imgCapture1, image;
    private Bitmap thumbnail;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private LinearLayout linearComponentsParent;
    private Order newOrder;
    private boolean isSerialNoSaved = false;

    private String GET_TEMPLATES_URL = "http://jigsawserverpink.com/admin/getTemplate.php";
    private String GET_SERIALNOS_URL = "http://jigsawserverpink.com/admin/getSerial.php";
    private String POST_SAVE_FOR_LATER_URL = "http://jigsawserverpink.com/admin/saveLaterOrder.php";
    private String POST_SUBMIT_ORDER_URL = "http://jigsawserverpink.com/admin/addOrder.php";
    private String POST_SUBMIT_ORDER_IMAGES_URL = "http://jigsawserverpink.com/admin/updateOrderImage.php";

    private ArrayList<CategoryEquipmentModel> categoryEquipmentModels;
    private ArrayList<Object> categoriesList = new ArrayList<>();

    private ArrayList<SerialNoModel> serialNoModels;

    ArrayList<Component> equipment;
    private CustomTemplateSpinnerAdapter mAdapter;
    private CustomSerialNoSpinnerAdapter mSAdapter;
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

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy HH:mm");
        String formattedDate = df.format(c.getTime());

        txtOrderDate.setText(formattedDate.toUpperCase());


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
                    newOrder.setCategory(((TemplateModel) spCategories.getSelectedItem()).getTemplateName());
                    newOrder.setCatSerialNumber(((SerialNoModel) spCatSerialNo.getSelectedItem()).serialNo);
                    newOrder.setComponents(getComponentsDetails());

                    saveForLaterPostCall();


                }
            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
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
                    newOrder.setCategory(((TemplateModel) spCategories.getSelectedItem()).getTemplateName());
                    newOrder.setCatSerialNumber(((SerialNoModel) spCatSerialNo.getSelectedItem()).serialNo);
                    newOrder.setComponents(getComponentsDetails());

                    submitOrderPostCAll(newOrder);
                }
            }
        });
    }

    private void init() {
        categories = new ArrayList<>();

        spCategories = (Spinner) findViewById(R.id.spCategories);
        spCatSerialNo = (Spinner) findViewById(R.id.spCatSerialNo);

        txtOrderDate = (TextView) findViewById(R.id.txtOrderDate);
        linearComponentsParent = (LinearLayout) findViewById(R.id.linearComponentsParent);


        imgBack = (TextView) findViewById(R.id.imgBack);

        txtSubmit = (TextView) findViewById(R.id.txtSubmit);
        txtSave = (TextView) findViewById(R.id.txtSave);

        getTemplates();
        getSerialNos();

       /* catSerialNos = new ArrayList<>();
        catSerialNos.add("JSW20091517-01");
        catSerialNos.add("JSW20091517-02");
        catSerialNos.add("JSW20091517-03");

        serialNoAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown, catSerialNos);
        serialNoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCatSerialNo.setAdapter(serialNoAdapter);*/

        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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

        for (int i = 0; i < components.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(NewRecordActivity.this);
            View inflatedLayout = inflater.inflate(R.layout.component_layout, null, false);
            linearComponentsParent.addView(inflatedLayout);

            TextView txtComponent1 = (TextView) inflatedLayout.findViewById(R.id.txtComponent1);
            txtComponent1.setText(components.get(i).getComponentName());
            imgCapture1 = (ImageView) inflatedLayout.findViewById(R.id.imgCapture1);
            imgComponent1 = (ImageView) inflatedLayout.findViewById(R.id.imgComponent1);
            //image = imgComponent1;

            ImageView removeImage = (ImageView) inflatedLayout.findViewById(R.id.imgRemove);


            imgCapture1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FrameLayout frame = (FrameLayout) ((LinearLayout) view.getParent()).getChildAt(0);
                    image = (ImageView) frame.findViewById(R.id.imgComponent1);
                    captureImage();
                    //image.setImageBitmap(thumbnail);
                }
            });

            removeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        image.setImageResource(android.R.color.transparent);
                    } catch (Exception e) {
                        Log.e("#### EXC",""+e.toString());
                    }
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
        if (componentsForTemplateSelected != null) {
            for (int i = 0; i < componentsForTemplateSelected.size(); i++) {
                View child = linearComponentsParent.getChildAt(i);
                String componentName = ((TextView) child.findViewById(R.id.txtComponent1)).getText().toString().trim();
                String componentDetails = ((EditText) child.findViewById(R.id.edtComponent1)).getText().toString().trim();
                ImageView componentImage = (ImageView) child.findViewById(R.id.imgComponent1);
                String componentPhoto = "";
                if (componentImage.getDrawable() != null) {
                    if (getImage(componentImage) != null) {
                        componentPhoto = Utility.returnBas64Image(getImage(componentImage));
                    }
                }
                Component component = new Component(componentName, componentDetails, componentPhoto);
                components.add(component);
            }
        }
        return components;
    }

    public Bitmap getImage(ImageView ivImage) {
        Bitmap bitmap;
        if (ivImage.getDrawable() instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
        } else {
            bitmap = null;
        }
        return bitmap;
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
                        JSONObject infoJSONObj = msg.getJSONObject("info");

                        // get all keys from categoryJSONObj

                        Iterator<String> iterator = infoJSONObj.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            //Log.e("TAG", "key:" + key + "--Value::" + infoJSONObj.optString(key));
                            Type listType = new TypeToken<List<CategoryEquipmentModel>>() {
                            }.getType();

                            categoryEquipmentModels = new GsonBuilder().create().fromJson(infoJSONObj.optString(key).toString(), listType);
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
                } catch (JSONException jsonEx) {
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

    private void getSerialNos() {

        final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
        circleDialog.setCancelable(true);
        circleDialog.show();

        new CallWebService(GET_SERIALNOS_URL, CallWebService.TYPE_JSONOBJECT) {

            @Override
            public void response(String response) {

                circleDialog.dismiss();

                Log.e("RESP serialNos_Details", response);

                try {
                    JSONObject msg = new JSONObject(response);

                    if (msg.getString("status").equals("1")) {
                        JSONArray data = msg.getJSONArray("data");

                        Type listType = new TypeToken<List<SerialNoModel>>() {
                        }.getType();

                        serialNoModels = new GsonBuilder().create().fromJson(data.toString(), listType);
                        Log.e("serialNoModels", serialNoModels.toString());

                        mSAdapter = new CustomSerialNoSpinnerAdapter(NewRecordActivity.this, serialNoModels, R.layout.spinner_dropdown, R.layout.spinner_layout);
                        spCatSerialNo.setAdapter(mSAdapter);
                    }
                } catch (JSONException jsonEx) {
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
        for (int i = 0; i < categoriesList.size(); i++) {
            ArrayList<CategoryEquipmentModel> list = (ArrayList<CategoryEquipmentModel>) categoriesList.get(i);
            TemplateModel template = new TemplateModel(list.get(0).categoryId, list.get(0).categoryName);
            templates.add(template);

            categories.add(list.get(0).categoryName);
            for (int j = 0; j < list.size(); j++) {
                Component component = new Component();
                component.setCategoryId(list.get(j).categoryId);
                component.setComponentName(list.get(j).equipmentName);
                component.setComponentDetails("");
                equipment.add(component);
            }
        }
        mAdapter = new CustomTemplateSpinnerAdapter(NewRecordActivity.this, templates, R.layout.spinner_dropdown, R.layout.spinner_layout);
        spCategories.setAdapter(mAdapter);

        Log.e("equipment", equipment.toString());
    }

    private void saveForLaterPostCall() {

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("s_no", ((SerialNoModel) spCatSerialNo.getSelectedItem()).serialNo));



            final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
            circleDialog.setCancelable(true);
            circleDialog.show();


        new GetPostClass(POST_SAVE_FOR_LATER_URL,pairs,EnumType.POST){

            @Override
            public void response(String msg) {
                circleDialog.dismiss();

                String response1 = msg.toString();
                Log.e("Resp SAVE_LATER: ", "" + response1);

                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(NewRecordActivity.this, "saved-record", 0);
                complexPreferences.putObject("saved-record", newOrder);
                complexPreferences.commit();

                Snackbar.make(txtSave, "Order Saved For Later Use!", Snackbar.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void error(String error) {
                isSerialNoSaved = false;
                Snackbar.make(txtSave, "Record Cannot Be Saved.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
                circleDialog.dismiss();
            }
        }.call();

    }

    private void submitOrderPostCAll(Order order) {

        Log.e("On call","POST");

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(NewRecordActivity.this, "user_pref", 0);
        UserProfile profile = complexPreferences.getObject("current-user", UserProfile.class);

        List<NameValuePair> pairs = new ArrayList<>();

        final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
        circleDialog.setCancelable(true);
        circleDialog.show();

        pairs.add(new BasicNameValuePair("o_date", order.getOrderDate()));
        pairs.add(new BasicNameValuePair("trolley_catg_name", order.getCategory()));

        StringBuilder equipmentDetails = new StringBuilder();
        final ArrayList<Component> components = order.getComponents();

        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            equipmentDetails.append("Equipment name: " + component.getComponentName() + " \n " +
                    "Equipment serial no.: " + component.getComponentDetails() + " \n ,");
        }

        pairs.add(new BasicNameValuePair("equipment_details", equipmentDetails.toString()));
        pairs.add(new BasicNameValuePair("adminName", profile.data.get(0).adminfname));
        pairs.add(new BasicNameValuePair("adminEmail", profile.data.get(0).adminemail));
        pairs.add(new BasicNameValuePair("serial_no", order.getCatSerialNumber()));


        new GetPostClass(POST_SUBMIT_ORDER_URL, pairs, EnumType.POST) {

            @Override
            public void response(String msg) {
                circleDialog.dismiss();

                String response1 = msg.toString();
                Log.e("Resp ORDER: ", "" + response1);

                try {
                    JSONObject response = new JSONObject(msg.toString());

                    if (!response.getString("msg").equals("0")) {
                        String generatedOrderID = response.getString("order_id");


                        Log.e("### comp size",""+components.size());
                        for (int i = 0; i < components.size(); i++) {
                            submitOrderImagesPostCall(generatedOrderID, components.get(i).getComponentPhoto());
                        }

                        Snackbar.make(txtSave, "Order Submitted Successfully.", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(txtSave, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Snackbar.make(txtSave, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    Log.e("EXCEPTION", e.toString());
                }
            }

            @Override
            public void error(String error) {
                Snackbar.make(txtSave, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
                circleDialog.dismiss();
            }
        }.call();
    }

    private void submitOrderImagesPostCall(String orderid, String img) {



        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("img", img));
        pairs.add(new BasicNameValuePair("order_id", orderid));



            final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
            circleDialog.setCancelable(true);
            circleDialog.show();


        new GetPostClass(POST_SUBMIT_ORDER_IMAGES_URL,pairs,EnumType.POST){

            @Override
            public void response(String msg) {
                circleDialog.dismiss();

                String response1 = msg.toString();
                Log.e("Resp ORDER_IMAGES: ", "" + response1);

                try {
                    JSONObject response = new JSONObject(msg.toString());

                    if (!response.getString("msg").equals("0")) {
                    } else {
                        Snackbar.make(txtSave, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Snackbar.make(txtSave, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    Log.e("EXCEPTION", e.toString());
                }


                finish();

            }

            @Override
            public void error(String error) {
                Snackbar.make(txtSave, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
                circleDialog.dismiss();
            }
        }.call();
    }


    // end of main class
}
