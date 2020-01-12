/*
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.project.emi.eventscape.core.managers;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.core.interactors.ProfileInteractor;
import com.project.emi.eventscape.core.managers.listeners.OnDataChangedListener;
import com.project.emi.eventscape.core.managers.listeners.OnObjectChangedListener;
import com.project.emi.eventscape.core.managers.listeners.OnObjectExistListener;
import com.project.emi.eventscape.core.managers.listeners.OnProfileCreatedListener;
import com.project.emi.eventscape.enums.ProfileStatus;
import com.project.emi.eventscape.models.User;

/**
 * Created by Kristina on 10/28/16.
 */

public class ProfileManager extends FirebaseListenersManager {

    private static final String TAG = ProfileManager.class.getSimpleName();
    private static ProfileManager instance;

    private Context context;
    private ProfileInteractor profileInteractor;


    public static ProfileManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileManager(context);
        }

        return instance;
    }

    private ProfileManager(Context context) {
        this.context = context;
        profileInteractor = ProfileInteractor.getInstance(context);
    }

    public User buildProfile(FirebaseUser firebaseUser, String largeAvatarURL) {
        User profile = new User();
        profile.setUser_id(firebaseUser.getUid());
        profile.setEmail(firebaseUser.getEmail());
        profile.setUsername(firebaseUser.getDisplayName());
        profile.setPhotoUrl(largeAvatarURL != null ? largeAvatarURL : firebaseUser.getPhotoUrl().toString());
        return profile;
    }

    public void isProfileExist(String id, final OnObjectExistListener<User> onObjectExistListener) {
        profileInteractor.isProfileExist(id, onObjectExistListener);
    }

    public void createOrUpdateProfile(User profile, OnProfileCreatedListener onProfileCreatedListener) {
        createOrUpdateProfile(profile, null, onProfileCreatedListener);
    }

    public void createOrUpdateProfile(final User profile, Uri imageUri, final OnProfileCreatedListener onProfileCreatedListener) {
        if (imageUri == null) {
            profileInteractor.createOrUpdateProfile(profile, onProfileCreatedListener);
        } else {
            profileInteractor.createOrUpdateProfileWithImage(profile, imageUri, onProfileCreatedListener);
        }
    }

    public void getProfileValue(Context activityContext, String id, final OnObjectChangedListener<User> listener) {
        ValueEventListener valueEventListener = profileInteractor.getProfile(id, listener);
        addListenerToMap(activityContext, valueEventListener);
    }

    public void getProfileSingleValue(String id, final OnObjectChangedListener<User> listener) {
        profileInteractor.getProfileSingleValue(id, listener);
    }

    public ProfileStatus checkProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return ProfileStatus.NOT_AUTHORIZED;
        }
//        } else if (!PreferencesUtil.isProfileCreated(context)) {
//            return ProfileStatus.NO_PROFILE;
//        }
       else {
            return ProfileStatus.PROFILE_CREATED;
        }
    }

    public void search(String searchText, OnDataChangedListener<User> onDataChangedListener) {
        closeListeners(context);
        ValueEventListener valueEventListener = profileInteractor.searchProfiles(searchText, onDataChangedListener);
        addListenerToMap(context, valueEventListener);
    }

    public void addRegistrationToken(String token, String userId) {
        profileInteractor.addRegistrationToken(token, userId);
    }
}
