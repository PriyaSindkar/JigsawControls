package com.jigsawcontrols.uiActivities;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jigsawcontrols.R;
import com.jigsawcontrols.uiFragments.ChangeAccessCodeFragment;
import com.jigsawcontrols.uiFragments.ChangePasswordFragment;
import com.jigsawcontrols.uiFragments.HomeFragment;


public class MyDrawerActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.frame, homeFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if(menuItem.isChecked()) menuItem.setChecked(false);
        else menuItem.setChecked(true);
        //Closing drawer on item click
        drawerLayout.closeDrawers();

        switch (menuItem.getItemId()) {
            case R.id.home:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment homeFragment = new HomeFragment();
                fragmentTransaction.replace(R.id.frame, homeFragment);
                fragmentTransaction.commit();
                    return true;
            case R.id.change_OTP:

                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentTransaction = fragmentManager1.beginTransaction();
                Fragment changeAccessCode = new ChangeAccessCodeFragment();
                fragmentTransaction.replace(R.id.frame, changeAccessCode);
                fragmentTransaction.commit();

                return true;
            case R.id.change_login_password:
                FragmentManager fragmentManager2 = getSupportFragmentManager();
                fragmentTransaction = fragmentManager2.beginTransaction();
                Fragment changePassword = new ChangePasswordFragment();
                fragmentTransaction.replace(R.id.frame, changePassword);
                fragmentTransaction.commit();
                return true;
            default:
                return true;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
