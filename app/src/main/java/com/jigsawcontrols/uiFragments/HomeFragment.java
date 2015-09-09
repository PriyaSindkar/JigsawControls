package com.jigsawcontrols.uiFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jigsawcontrols.R;

public class HomeFragment extends Fragment {


    public static HomeFragment newInstance() {
        HomeFragment f = new HomeFragment();
        return f;
    }


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_quick_acess, container, false);


        return  view;
    }


// end of main class
}
