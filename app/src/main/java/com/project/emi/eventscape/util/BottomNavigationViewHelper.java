package com.project.emi.eventscape.util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.core.managers.ProfileManager;
import com.project.emi.eventscape.domain.home.HomeActivity;
import com.project.emi.eventscape.domain.newprofile.NewProfileActivity;
import com.project.emi.eventscape.domain.profile.ProfileActivity;
import com.project.emi.eventscape.domain.notification.NotificationActivity;
import com.project.emi.eventscape.domain.search.SearchActivity;

import com.project.emi.eventscape.share.ShareActivity;

public class BottomNavigationViewHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
//        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }
    public static void enableNavigation(final Context context, BottomNavigationViewEx view, int currentIndex){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ic_house:
                        if(currentIndex!=0) {
                            Intent intent_h = new Intent(context, HomeActivity.class);
                            context.startActivity(intent_h);
                        }
                        break;
                    case R.id.ic_search:
                        if(currentIndex!=1) {
                            Intent intent_s = new Intent(context, SearchActivity.class);
                            context.startActivity(intent_s);
                        }
                        break;
                    case R.id.ic_circle:
                        if(currentIndex!=2) {
                            Intent intent_c = new Intent(context, ShareActivity.class);
                            context.startActivity(intent_c);
                        }
                        break;
                    case R.id.ic_alert:
                        if(currentIndex!=3) {
                            Intent intent_a = new Intent(context, NotificationActivity.class);
                            context.startActivity(intent_a);
                        }
                        break;
                    case R.id.ic_android:
                        if(currentIndex!=4) {
                            Intent intent_p = new Intent(context, NewProfileActivity.class);
                            intent_p.putExtra(ProfileActivity.USER_ID_EXTRA_KEY, PreferencesUtil.getUserUid(context));
                            context.startActivity(intent_p);
                        }
                        break;
                }
                return false;
            }
        });
    }
}
