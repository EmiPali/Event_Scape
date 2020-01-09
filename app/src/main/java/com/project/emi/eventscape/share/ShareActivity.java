package com.project.emi.eventscape.share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.util.BottomNavigationViewHelper;
import com.project.emi.eventscape.util.Permissions;
import com.project.emi.eventscape.adapters.SectionsPagerAdapter;


public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private Context mContext = ShareActivity.this;
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started");

        if (checkPermissionsArray(Permissions.PERMISSIONS)){
            setUpViewPager();
        }
        else{
            verifyPermissions(Permissions.PERMISSIONS);
        }


        //setupBottomNavigationView();

    }

    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    private void setUpViewPager()
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
    }


    public void verifyPermissions (String[] permissions){
        Log.d(TAG,"verifyPermissions : verifying permissions");
        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */


    public boolean checkPermissionsArray(String[] permissions)
    {
        Log.d(TAG,"checkPermissionsArray: checking permissions array.");
        for (int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if (!checkPermissions(check)){
                return false;
            }
        }return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */


    public boolean checkPermissions(String permission)
    {
        Log.d(TAG,"checkPermissions: checking permission:" + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"checkPermissions : \n permission was not granted for :" + permission);
            return false;
        }
        else {
            Log.d(TAG,"checkPermissions : \n permission was granted for :" + permission);
            return true;
        }
    }



    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        Log.d("this is null "+bottomNavigationViewEx+" or not ", "setupBottomNavigationView: setting up BottomNavigationView");
        //BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
}
