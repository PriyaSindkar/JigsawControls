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
import com.jigsawcontrols.helpers.PrefUtils;
import com.jigsawcontrols.helpers.Utility;
import com.jigsawcontrols.model.CategoryEquipmentModel;
import com.jigsawcontrols.model.CategoryEquipmentModel2;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OldRecordActivity extends AppCompatActivity {

    private TextView txtOrderDate, imgBack,txtSubmit,txtSerialNo ;
    private Spinner spCategories;
    ImageView removeImage;
    private List<String> categories, catSerialNos;
    private ArrayAdapter<String> adapter;
    private Order savedOrder;
    private ImageView imgComponent1, imgCapture1, image;
    private Bitmap thumbnail;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private LinearLayout linearComponentsParent;
    private Order newOrder;
    private int noOfComponents;
    ComplexPreferences complexPreferences;
    UserProfile profile;
    private boolean isFirstTime = true;
    EditText ednotes;
    private String GET_TEMPLATES_URL = "http://jigsawserverpink.com/admin/getTemplate.php";
    private String POST_SUBMIT_ORDER_URL = "http://jigsawserverpink.com/admin/addOrder.php";
    private String POST_SUBMIT_ORDER_IMAGES_URL = "http://jigsawserverpink.com/admin/updateOrderImage.php";

    private ArrayList<CategoryEquipmentModel2> categoryEquipmentModels;
    private ArrayList<Object> categoriesList = new ArrayList<>();
    private ArrayList<Component> componentsForTemplateSelected;

    ArrayList<Component> equipment;
    private CustomTemplateSpinnerAdapter mAdapter;
    private CustomSerialNoSpinnerAdapter mSAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_old_record);

        PrefUtils.saveTime(OldRecordActivity.this, PrefUtils.getCurrentDateTime());

        complexPreferences = ComplexPreferences.getComplexPreferences(OldRecordActivity.this, "saved-record", 0);
        savedOrder = complexPreferences.getObject("saved-record", Order.class);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(OldRecordActivity.this, "user_pref", 0);
        profile = complexPreferences.getObject("current-user", UserProfile.class);

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

    @Override
       protected void onResume() {
        super.onResume();

        if(PrefUtils.isTimeMoreThan1Hour(PrefUtils.getCurrentDateTime(),PrefUtils.returnTime(OldRecordActivity.this))){
            //Toast.makeText(MyDrawerActivity.this,"More than 1 hour",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(OldRecordActivity.this,QuickAccessActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }else{
            // Toast.makeText(MyDrawerActivity.this,"Less than 1 hour",Toast.LENGTH_SHORT).show();
        }


    }

    private void init() {

        ednotes = (EditText)findViewById(R.id.ednotes);

        txtOrderDate = (TextView) findViewById(R.id.txtOrderDate);
        txtSerialNo = (TextView) findViewById(R.id.txtSerialNo);
        txtSubmit = (TextView) findViewById(R.id.txtSubmit);
        TextView txtSubmit = (TextView) findViewById(R.id.txtSubmit);
      /*  txtSubmit.setVisibility(View.GONE);
        View line = findViewById(R.id.line);
        line.setVisibility(View.GONE);*/

        imgBack = (TextView) findViewById(R.id.imgBack);
        imgBack.setText("Incomplete Order");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        linearComponentsParent = (LinearLayout) findViewById(R.id.linearComponentsParent);

        categories = new ArrayList<>();

        spCategories = (Spinner) findViewById(R.id.spCategories);

        getTemplates();

        /*txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complexPreferences.clearObject();
                complexPreferences.commit();
                Snackbar.make(view, "Order Placed!", Snackbar.LENGTH_LONG).show();
            }
        });*/

        spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count=0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(count>1) {
                    Log.e("TAG", "setOnItemSelectedListener");
                    if (spCategories.getItemAtPosition(i) != null) {
                        String templateId = ((TemplateModel) mAdapter.getItem(i)).getTemplateId();
                        componentsForTemplateSelected = new ArrayList<Component>();
                        for (int c = 0; c < equipment.size(); c++) {
                            if (equipment.get(c).getCategoryId().equals(templateId)) {
                                componentsForTemplateSelected.add(equipment.get(c));
                            }
                        }

                     addImageLayout(componentsForTemplateSelected);
                     noOfComponents = componentsForTemplateSelected.size();
                    }
                }
                count++;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (spCategories.getSelectedItem().toString().trim().equals("Select Trolley Category")) {
                    Snackbar snack = Snackbar.make(spCategories, "Please Select a Category!", Snackbar.LENGTH_SHORT);
                    snack.show();
                } else {
                    newOrder = new Order();
                    newOrder.setOrderDate(txtOrderDate.getText().toString().trim());
                    newOrder.setCategory(txtSerialNo.getText().toString());
                    newOrder.setCatSerialNumber(((TemplateModel) spCategories.getSelectedItem()).getSerialNumber());

                    newOrder.setNotes(ednotes.getText().toString().trim());
                    newOrder.setComponents(getComponentsDetails());

                    submitOrderPostCAll(newOrder);


                }
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
            noOfComponents = savedOrder.getComponents().size();
            spCategories.setSelection(getTemplateIndex(spCategories, savedOrder.getCategory()));
            txtSerialNo.setText(savedOrder.getCatSerialNumber());
            ednotes.setText(savedOrder.getNotes());
            txtOrderDate.setText(savedOrder.getOrderDate());
            addImageLayout(savedOrder.getComponents());
        }
    }

    private void addImageLayout(ArrayList<Component> savedOrderComponents) {
        Log.e("TAG", "IMAGE CALL");
            linearComponentsParent.removeAllViews();

            for(int i=0; i<savedOrderComponents.size(); i++) {
                LayoutInflater inflater = LayoutInflater.from(OldRecordActivity.this);
                View inflatedLayout = inflater.inflate(R.layout.component_layout, null, false);

                TextView txtComponent1 = (TextView) inflatedLayout.findViewById(R.id.txtComponent1);
                txtComponent1.setText(savedOrderComponents.get(i).getComponentName());

                EditText edtComponent1 = (EditText) inflatedLayout.findViewById(R.id.edtComponent1);
                edtComponent1.setText(savedOrderComponents.get(i).getComponentDetails());

                imgCapture1 = (ImageView) inflatedLayout.findViewById(R.id.imgCapture1);
                imgComponent1 = (ImageView) inflatedLayout.findViewById(R.id.imgComponent1);

                removeImage = (ImageView)  inflatedLayout.findViewById(R.id.imgRemove);
                removeImage.setVisibility(View.GONE);
                removeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        image.setImageResource(android.R.color.transparent);
                    }
                });

                if(savedOrderComponents.get(i).getComponentPhoto() != null && !savedOrderComponents.get(i).getComponentPhoto().equals("")) {
                    imgComponent1.setImageBitmap(getImageBitmapFromString(savedOrderComponents.get(i).getComponentPhoto()));
                    removeImage.setVisibility(View.VISIBLE);
                } else {
                    removeImage.setVisibility(View.GONE);
                }



                imgCapture1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FrameLayout frame = (FrameLayout)((LinearLayout) view.getParent()).getChildAt(0);
                        image = (ImageView)  frame.findViewById(R.id.imgComponent1);
                        captureImage();
                    }
                });
                linearComponentsParent.addView(inflatedLayout);
            }
    }

    private ArrayList<Component> getComponentsDetails() {
        ArrayList<Component> components = new ArrayList<>();
        // int noOfComponets = linearComponentsParent.getChildCount();

        for(int i=0; i< noOfComponents; i++) {
            View child = linearComponentsParent.getChildAt(i);
            String componentName = ((TextView) child.findViewById(R.id.txtComponent1)).getText().toString().trim();
            String componentDetails = ((EditText) child.findViewById(R.id.edtComponent1)).getText().toString().trim();
            ImageView componentImage = (ImageView) child.findViewById(R.id.imgComponent1);
            String componentPhoto = "";
            if (componentImage.getDrawable() != null) {
                if(getImage(componentImage) != null) {
                    componentPhoto = Utility.returnBas64Image(getImage(componentImage));
                }
            }
            Component component = new Component(componentName, componentDetails, componentPhoto);
            components.add(component);
        }
        return components;
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
                            Type listType = new TypeToken<List<CategoryEquipmentModel2>>() {
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

    private void submitOrderPostCAll(Order order) {

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(OldRecordActivity.this, "user_pref", 0);
        UserProfile profile = complexPreferences.getObject("current-user", UserProfile.class);

        List<NameValuePair> pairs = new ArrayList<>();

        final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
        circleDialog.setCancelable(true);
        circleDialog.show();



        pairs.add(new BasicNameValuePair("o_date", order.getOrderDate()));
        pairs.add(new BasicNameValuePair("trolley_catg_name", order.getCategory()));

        StringBuilder equipmentDetails = new StringBuilder();
        final ArrayList<Component> components = order.getComponents();

        Log.e("REQ components: ", "" + components.toString());

        for(int i=0; i<components.size(); i++) {
            Component component = components.get(i);
            equipmentDetails.append("Equipment name: "+component.getComponentName()+" \n "+
                    "Equipment serial no.: "+component.getComponentDetails()+" \n ,");
        }

        pairs.add(new BasicNameValuePair("equipment_details", equipmentDetails.toString()));
        pairs.add(new BasicNameValuePair("notes", order.getNotes()));
        pairs.add(new BasicNameValuePair("adminName", profile.data.get(0).adminfname));
        pairs.add(new BasicNameValuePair("adminEmail", profile.data.get(0).adminemail));
        pairs.add(new BasicNameValuePair("serial_no", order.getCatSerialNumber()));

        Log.e("REQ ORDER: ", "" + pairs.toString());


        new GetPostClass(POST_SUBMIT_ORDER_URL, pairs, EnumType.POST) {

            @Override
            public void response(String msg) {
                circleDialog.dismiss();

                String response1 = msg.toString();
                Log.e("Resp ORDER: ", "" + response1);

                try {
                    JSONObject response = new JSONObject(msg.toString());

                    if ( !response.getString("msg").equals("0")) {
                        String generatedOrderID = response.getString("order_id");

                        for(int i=0; i<components.size(); i++) {
                            submitOrderImagesPostCall(generatedOrderID, components.get(i).getComponentPhoto());
                        }

                        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(OldRecordActivity.this, "saved-record", 0);
                        complexPreferences.clearObject();
                        complexPreferences.commit();

                        Snackbar.make(txtSubmit, "Order Submitted Successfully.", Snackbar.LENGTH_LONG).show();

                        finish();
                    } else {
                        Snackbar.make(txtSubmit, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Snackbar.make(txtSubmit, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    Log.e("EXCEPTION", e.toString());
                }
            }

            @Override
            public void error(String error) {
                Snackbar.make(txtSubmit, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
                circleDialog.dismiss();
            }
        }.call();
    }

    private void submitOrderImagesPostCall(String orderid, String img) {



        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("img", img));
        pairs.add(new BasicNameValuePair("order_id", orderid));

        Log.e("REQ images: ", "" + pairs.toString());

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
                        Snackbar.make(txtSubmit, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Snackbar.make(txtSubmit, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                    Log.e("EXCEPTION", e.toString());
                }


                finish();

            }

            @Override
            public void error(String error) {
                Snackbar.make(txtSubmit, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
                circleDialog.dismiss();
            }
        }.call();
    }

    private void setCategories() {
        ArrayList<TemplateModel> templates = new ArrayList<>();
        categories.clear();
        equipment = new ArrayList<>();
        for(int i=0; i<categoriesList.size(); i++) {
            ArrayList<CategoryEquipmentModel2> list = (ArrayList<CategoryEquipmentModel2>) categoriesList.get(i);
            TemplateModel template = new TemplateModel(list.get(0).template_id, list.get(0).template_name,list.get(0).number);
            templates.add(template);

            categories.add(list.get(0).template_name);
            for (int j = 0; j < list.size(); j++) {
                Component component = new Component();
                component.setCategoryId(list.get(j).template_id);
                component.setComponentName(list.get(j).equipment_name);
                component.setComponentDetails("");
                equipment.add(component);
            }
        }
        mAdapter = new CustomTemplateSpinnerAdapter(OldRecordActivity.this, templates,R.layout.spinner_dropdown, R.layout.spinner_layout );
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
        removeImage.setVisibility(View.VISIBLE);

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
