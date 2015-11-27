package com.jigsawcontrols.uiActivities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.jigsawcontrols.R;
import com.jigsawcontrols.helpers.PrefUtils;

/**
 * Created by Krishna on 27-11-2015.
 */
public class EditOrder extends Activity {

    TextView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);
        init();

    }

    private void init() {

        imgBack = (TextView)findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//end of main class;
}
