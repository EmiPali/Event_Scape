package com.project.emi.eventscape.domain.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.core.managers.ProfileManager;
import com.project.emi.eventscape.enums.ProfileStatus;

public class BasePresenter<T extends BaseView & MvpView> extends MvpBasePresenter<T> {

    protected String TAG = this.getClass().getSimpleName();

    protected Context context;
    private ProfileManager profileManager;

    public BasePresenter(Context context) {
        this.context = context;
        profileManager = ProfileManager.getInstance(context);
    }

    public boolean checkInternetConnection() {
        return checkInternetConnection(null);
    }

    public boolean checkInternetConnection(@Nullable View anchorView) {
        boolean hasInternetConnection = hasInternetConnection();
        if (!hasInternetConnection) {
            ifViewAttached(view -> {
                if (anchorView != null) {
                    view.showSnackBar(anchorView, R.string.internet_connection_failed);
                } else {
                    view.showSnackBar(R.string.internet_connection_failed);
                }
            });
        }

        return hasInternetConnection;
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public boolean checkAuthorization(){
        ProfileStatus profileStatus = profileManager.checkProfile();
        if (profileStatus.equals(ProfileStatus.NOT_AUTHORIZED) || profileStatus.equals(ProfileStatus.NO_PROFILE)) {
            ifViewAttached(BaseView::startLoginActivity);
            return false;
        } else {
            return true;
        }
    }

    public void doAuthorization(ProfileStatus status) {
        if (status.equals(ProfileStatus.NOT_AUTHORIZED) || status.equals(ProfileStatus.NO_PROFILE)) {
            ifViewAttached(BaseView::startLoginActivity);
        }
    }

    protected String getCurrentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

}
