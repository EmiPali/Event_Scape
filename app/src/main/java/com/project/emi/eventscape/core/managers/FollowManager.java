package com.project.emi.eventscape.core.managers;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.core.interactors.FollowInteractor;
import com.project.emi.eventscape.core.managers.listeners.OnCountChangedListener;
import com.project.emi.eventscape.core.managers.listeners.OnDataChangedListener;
import com.project.emi.eventscape.core.managers.listeners.OnObjectExistListener;
import com.project.emi.eventscape.core.managers.listeners.OnRequestComplete;
import com.project.emi.eventscape.enums.FollowState;
import com.project.emi.eventscape.util.LogUtil;


public class FollowManager extends FirebaseListenersManager {

    private static final String TAG = FollowManager.class.getSimpleName();
    private static FollowManager instance;
    private FollowInteractor followInteractor;

    private Context context;

    public static FollowManager getInstance(Context context) {
        if (instance == null) {
            instance = new FollowManager(context);
        }

        return instance;
    }

    private FollowManager(Context context) {
        this.context = context;
        followInteractor = FollowInteractor.getInstance(context);
    }

    public void checkFollowState(String myId, String userId, CheckStateListener checkStateListener) {
        doesUserFollowMe(myId, userId, userFollowMe -> {

            doIFollowUser(myId, userId, iFollowUser -> {
                FollowState followState;

                if (userFollowMe && iFollowUser) {
                    followState = FollowState.FOLLOW_EACH_OTHER;
                } else if (userFollowMe) {
                    followState = FollowState.USER_FOLLOW_ME;
                } else if (iFollowUser) {
                    followState = FollowState.I_FOLLOW_USER;
                } else {
                    followState = FollowState.NO_ONE_FOLLOW;
                }

                checkStateListener.onStateReady(followState);

                LogUtil.logDebug(TAG, "checkFollowState, state: " + followState);
            });
        });
    }


    public void doesUserFollowMe(String myId, String userId, final OnObjectExistListener onObjectExistListener) {
        followInteractor.isFollowingExist(userId, myId, onObjectExistListener);
    }

    public void doIFollowUser(String myId, String userId, final OnObjectExistListener onObjectExistListener) {
        followInteractor.isFollowingExist(myId, userId, onObjectExistListener);
    }

    public void followUser(Activity activity, String currentUserId, String targetUserId, OnRequestComplete onRequestComplete) {
        followInteractor.followUser(activity, currentUserId, targetUserId, onRequestComplete);
    }

    public void unfollowUser(Activity activity, String currentUserId, String targetUserId, OnRequestComplete onRequestComplete) {
        followInteractor.unfollowUser(activity, currentUserId, targetUserId, onRequestComplete);
    }

    public void getFollowersCount(Context activityContext, String targetUserId, OnCountChangedListener onCountChangedListener) {
        ValueEventListener listener = followInteractor.getFollowersCount(targetUserId, onCountChangedListener);
        addListenerToMap(activityContext, listener);
    }

    public void getFollowingsCount(Context activityContext, String targetUserId, OnCountChangedListener onCountChangedListener) {
        ValueEventListener listener = followInteractor.getFollowingsCount(targetUserId, onCountChangedListener);
        addListenerToMap(activityContext, listener);
    }

    public void getFollowingsIdsList(String targetUserId,
                                     OnDataChangedListener<String> onDataChangedListener) {
        followInteractor.getFollowingsList(targetUserId, onDataChangedListener);
    }

    public void getFollowersIdsList(String targetUserId,
                                    OnDataChangedListener<String> onDataChangedListener) {
        followInteractor.getFollowersList(targetUserId, onDataChangedListener);
    }

    public interface CheckStateListener {
        void onStateReady(FollowState followState);
    }
}
