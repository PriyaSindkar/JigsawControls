package com.jigsawcontrols.uiFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jigsawcontrols.R;
import com.jigsawcontrols.helpers.PrefUtils;
import com.jigsawcontrols.uiActivities.HistoryActivity;
import com.jigsawcontrols.uiActivities.LoginActivity;
import com.jigsawcontrols.uiActivities.NewRecordActivity;
import com.jigsawcontrols.uiActivities.OldRecordActivity;
import com.jigsawcontrols.uiActivities.QuickAccessActivity;

public class HomeFragment extends Fragment {

    LinearLayout linearNewRecord,linearEditRecord, linearOldRecord, linearHistory, btnLogout;

    public static HomeFragment newInstance() {
        HomeFragment f = new HomeFragment();
        return f;
    }


    public HomeFragment() {
        // Required empty public constructor
    }

    /*@Override
    public void onStart() {
        super.onStart();
        PrefUtils.saveTime(getActivity(), PrefUtils.getCurrentDateTime());
    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getActivity(), "Less than 1 hour", Toast.LENGTH_SHORT);
        if(PrefUtils.isTimeMoreThan1Hour(PrefUtils.getCurrentDateTime(),PrefUtils.returnTime((getActivity())))){
            Toast.makeText(getActivity(), "More than 1 hour", Toast.LENGTH_SHORT);
        }else{
            Toast.makeText(getActivity(),"Less than 1 hour",Toast.LENGTH_SHORT);
        }


    }

*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.framgment_home, container, false);
        btnLogout = (LinearLayout) view.findViewById(R.id.btnLogout);
        linearNewRecord = (LinearLayout) view.findViewById(R.id.linearNewRecord);
        linearOldRecord = (LinearLayout) view.findViewById(R.id.linearOldRecord);
        linearHistory = (LinearLayout) view.findViewById(R.id.linearHistory);
        linearEditRecord  = (LinearLayout) view.findViewById(R.id.linearEditRecord);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("isUserLogin");
                editor.commit();

                Intent intent = new Intent(getActivity(), QuickAccessActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        linearNewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewRecordActivity.class);
                startActivity(intent);
            }
        });

        linearOldRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OldRecordActivity.class);
                startActivity(intent);
            }
        });

        linearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        return  view;
    }


// end of main class
}
