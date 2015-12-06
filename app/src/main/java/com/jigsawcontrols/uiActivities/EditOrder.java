package com.jigsawcontrols.uiActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jigsawcontrols.R;
import com.jigsawcontrols.apiHelpers.EnumType;
import com.jigsawcontrols.apiHelpers.GetPostClass;
import com.jigsawcontrols.helpers.ComplexPreferences;
import com.jigsawcontrols.helpers.PrefUtils;
import com.jigsawcontrols.model.Component;
import com.jigsawcontrols.model.OrderHistoryModel;
import com.jigsawcontrols.model.UserProfile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Krishna on 27-11-2015.
 */
public class EditOrder extends Activity {

    EditText edSerialNo,ednotes;
    ScrollView scrollView;
    TextView imgBack,txtSend,txtSave;
    LinearLayout linearComponentsParent;
    private ArrayList<OrderHistoryModel> orderHistoryModels;
    // pass serial no only
    // key : "sno"
    private final String GET_ORDER_SERIAL = "http://jigsawserverpink.com/admin/getHistoryBySerial.php";



    // to edit order
    // key : "sno"
    // "o_date"
    // "equipment_details"
    // "notes"
    private final String EDIT_ORDER_SERIAL = "http://jigsawserverpink.com/admin/editOrder.php";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);
        init();

    }


    private void init() {

        txtSave= (TextView)findViewById(R.id.txtSave);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        ednotes = (EditText)findViewById(R.id.ednotes);
        imgBack = (TextView)findViewById(R.id.imgBack);
        txtSend = (TextView)findViewById(R.id.txtSend);
        edSerialNo = (EditText)findViewById(R.id.edSerialNo);
        linearComponentsParent = (LinearLayout)findViewById(R.id.linearComponentsParent);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edSerialNo.getText().toString().trim().length() == 0) {
                    Toast.makeText(EditOrder.this, "Please enter serial number first !!!", Toast.LENGTH_SHORT).show();
                } else {
                    getData();
                }
            }
        });


        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder equipmentDetails = new StringBuilder();
                boolean isAllValidate =  true;


                for (int i = 0; i < linearComponentsParent.getChildCount(); i++) {

                   View inflatedLayout = linearComponentsParent.getChildAt(i);

                    EditText edItem = (EditText) inflatedLayout.findViewById(R.id.edItem);
                    TextView txtTitle = (TextView) inflatedLayout.findViewById(R.id.txtTitle);

                    if(edItem.getText().toString().trim().length()==0){
                        isAllValidate = false;

                    }else{
                        equipmentDetails.append(txtTitle.getText().toString() + " \n " +
                                "Equipment serial no.: " + edItem.getText().toString().trim() + " \n ,");
                    }

                }

               // if(isAllValidate){

                    /*if(ednotes.getText().toString().trim().length()==0){
                        Snackbar.make(findViewById(android.R.id.content), "Please add some notes also.", Snackbar.LENGTH_LONG).show();
                    }else{*/
                        sendData(equipmentDetails.toString(),ednotes.getText().toString().trim());
                    //}

               // }else {
                 //   Snackbar.make(findViewById(android.R.id.content), "Please fill all equipment details.", Snackbar.LENGTH_LONG).show();
               // }





            }
        });
    }

    private void sendData(String equipmentDetails,String notes){
        final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
        circleDialog.setCancelable(true);
        circleDialog.show();

        List<NameValuePair> pairs = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy HH:mm");
        String formattedDate = df.format(c.getTime());


        pairs.add(new BasicNameValuePair("o_date",formattedDate.toUpperCase()));
        pairs.add(new BasicNameValuePair("equipment_details", equipmentDetails.toString()));
        pairs.add(new BasicNameValuePair("notes", notes));
        pairs.add(new BasicNameValuePair("sno", edSerialNo.getText().toString().trim()));

        new GetPostClass(EDIT_ORDER_SERIAL, pairs, EnumType.POST) {

            @Override
            public void response(String msg) {
                circleDialog.dismiss();

                String response1 = msg.toString();
                Log.e("Resp ORDER: ", "" + response1);

                try {
                    JSONObject response = new JSONObject(msg.toString());

                    if (!response.getString("msg").equals("0")) {

                        Toast.makeText(EditOrder.this, "Order Submitted Successfully.", Toast.LENGTH_SHORT).show();

                        //Snackbar.make(txtSave, "Order Submitted Successfully.", Snackbar.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EditOrder.this, "Order Submitted Failed.", Toast.LENGTH_SHORT).show();


                       // Snackbar.make(txtSave, "Order Submission Failed.", Snackbar.LENGTH_LONG).show();
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

    private void getData(){

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(EditOrder.this, "user_pref", 0);
        UserProfile profile = complexPreferences.getObject("current-user", UserProfile.class);


        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("sno", edSerialNo.getText().toString().trim()));

        final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
        circleDialog.setCancelable(true);
        circleDialog.show();

        new GetPostClass(GET_ORDER_SERIAL, pairs, EnumType.POST) {

            @Override
            public void response(String msg) {
                circleDialog.dismiss();
                try {
                    JSONObject response = new JSONObject(msg.toString());
                    JSONArray jarray = response.getJSONArray("data");

                    Log.e("Resp HISTORY: ", "" + msg);

                    if (jarray.length()!=0) {

                        txtSave.setVisibility(View.VISIBLE);
                        ednotes.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.VISIBLE);
                        JSONArray data = response.getJSONArray("data");
                        Type listType = new TypeToken<List<OrderHistoryModel>>() {
                        }.getType();

                        orderHistoryModels =  new GsonBuilder().create().fromJson(data.toString(), listType);
                        Log.e("orderHistoryModels", orderHistoryModels.size()+"");

                        setData();

                    } else {
                        txtSave.setVisibility(View.GONE);
                        ednotes.setVisibility(View.GONE);
                        scrollView.setVisibility(View.GONE);
                        Snackbar.make(findViewById(android.R.id.content), "Invalid Serial number.", Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Snackbar.make(findViewById(android.R.id.content), "Cannot Fetch Data.", Snackbar.LENGTH_LONG).show();
                    Log.e("EXCEPTION", e.toString());
                }
            }

            @Override
            public void error(String error) {

                circleDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Cannot Fetch History.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
            }
        }.call();
    }

    private void setData() {

        // key : "sno"
        // "o_date"
        // "equipment_details"
        // "notes"
        String[] equipments = orderHistoryModels.get(0).equipmentDetails.split(",");

        linearComponentsParent.removeAllViews();
        for(int i=0;i<equipments.length;i++){

            String[] data = equipments[i].split("\n");

            LayoutInflater inflater = LayoutInflater.from(EditOrder.this);
            View inflatedLayout = inflater.inflate(R.layout.row_item, null, false);

            EditText edItem = (EditText) inflatedLayout.findViewById(R.id.edItem);
            TextView txtTitle = (TextView)inflatedLayout.findViewById(R.id.txtTitle);


            String serailno = data[1];
            serailno = serailno.replace("Equipment serial no.:","");

            edItem.setText(serailno.trim());
            txtTitle.setText(data[0]);


            linearComponentsParent.addView(inflatedLayout);
        }




    }

//end of main class;
}
