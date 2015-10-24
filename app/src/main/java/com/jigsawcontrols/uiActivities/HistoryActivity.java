package com.jigsawcontrols.uiActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
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
import com.jigsawcontrols.apiHelpers.EnumType;
import com.jigsawcontrols.apiHelpers.GetPostClass;
import com.jigsawcontrols.apiHelpers.MyApplication;
import com.jigsawcontrols.model.CategoryEquipmentModel;
import com.jigsawcontrols.model.OrderHistoryModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class HistoryActivity extends ActionBarActivity {
    private LinearLayout linearComponents;
    private TextView imgBack;
    private final String HISTORY_POST_URL = "http://jigsawserverpink.com/admin/getHistory.php";
    private ArrayList<OrderHistoryModel> orderHistoryModels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_history);

        linearComponents = (LinearLayout) findViewById(R.id.linearComponents);
        imgBack = (TextView) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getHistory();
    }


    private void getHistory() {

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("email", "test@gmail.com"));


            final ProgressDialog circleDialog = ProgressDialog.show(this, "Please wait", "Loading...", true);
            circleDialog.setCancelable(true);
            circleDialog.show();

        new GetPostClass(HISTORY_POST_URL, pairs, EnumType.POST) {

            @Override
            public void response(String msg) {
                circleDialog.dismiss();
                try {
                    JSONObject response = new JSONObject(msg.toString());

                    Log.e("Resp HISTORY: ", "" + msg);

                    if ( response.getString("data") != null && !response.getString("data").equals("0")) {
                        JSONArray data = response.getJSONArray("data");
                        Type listType = new TypeToken<List<OrderHistoryModel>>() {
                        }.getType();

                        orderHistoryModels =  new GsonBuilder().create().fromJson(data.toString(), listType);
                        Log.e("orderHistoryModels", orderHistoryModels.size()+"");

                        setHistory();

                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Cannot Fetch History.", Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Snackbar.make(findViewById(android.R.id.content), "Cannot Fetch History.", Snackbar.LENGTH_LONG).show();
                    Log.e("EXCEPTION", e.toString());
                }
            }

            @Override
            public void error(String error) {
                Snackbar.make(findViewById(android.R.id.content), "Cannot Fetch History.", Snackbar.LENGTH_LONG).show();
                Log.e("VOLLEY EXCEPTION", error.toString());
            }
        }.call();


    }

    private void setHistory() {
        //linearComponents.removeAllViews();

        for(int i=0; i<orderHistoryModels.size(); i++) {
            Log.e("order" , orderHistoryModels.get(i).toString());

            LayoutInflater inflater = LayoutInflater.from(HistoryActivity.this);
            View inflatedLayout = inflater.inflate(R.layout.history_order_details, null, false);

            TextView txtOrderId = (TextView) inflatedLayout.findViewById(R.id.txtOrderId);
            txtOrderId.setText("Order Id: "+orderHistoryModels.get(i).orderId);

            TextView txtTrolleyCategory = (TextView) inflatedLayout.findViewById(R.id.txtTrolleyCategory);
            txtTrolleyCategory.setText("Category Name: "+orderHistoryModels.get(i).trolleyCategoryName);
            TextView txtSerialNo = (TextView) inflatedLayout.findViewById(R.id.txtSerialNo);
            txtSerialNo.setText("Serial No.: "+orderHistoryModels.get(i).serialNo);
            TextView txtOrderDate = (TextView) inflatedLayout.findViewById(R.id.txtOrderDate);
            txtOrderDate.setText("Order Date: "+orderHistoryModels.get(i).orderDate);

            LinearLayout linearEquipments = (LinearLayout) inflatedLayout.findViewById(R.id.linearEquipments);

            String[] equipments = orderHistoryModels.get(i).equipmentDetails.split(",");

            for(int j=0; j< equipments.length; j++) {
                inflater = LayoutInflater.from(HistoryActivity.this);
                View equipmentDetailsView = inflater.inflate(R.layout.equipment_details, null, false);
                ImageView imgEquipment = (ImageView) equipmentDetailsView.findViewById(R.id.imgEquipment);
                TextView txtEqipmentSerialNo = (TextView) equipmentDetailsView.findViewById(R.id.txtEqipmentSerialNo);

              //  String[] details = equipments[j].split("\n");
                txtEqipmentSerialNo.setText("Equipment Serial No.: " + equipments[j]);
                TextView txtEqipmentDetails = (TextView) equipmentDetailsView.findViewById(R.id.txtEqipmentDetails);
               // txtEqipmentDetails.setText("Equipment Serial No.: " + details[0]);
                linearEquipments.addView(equipmentDetailsView);
            }

            if( !orderHistoryModels.get(i).orderId.equals("")) {

                linearComponents.addView(inflatedLayout);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
