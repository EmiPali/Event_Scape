
package com.project.emi.eventscape.adapters.holders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.core.managers.FollowManager;
import com.project.emi.eventscape.core.managers.ProfileManager;
import com.project.emi.eventscape.core.managers.listeners.OnObjectChangedListener;
import com.project.emi.eventscape.core.managers.listeners.OnObjectChangedListenerSimple;
import com.project.emi.eventscape.enums.FollowState;
import com.project.emi.eventscape.models.User;
import com.project.emi.eventscape.util.GlideApp;
import com.project.emi.eventscape.util.ImageUtil;
import com.project.emi.eventscape.views.FollowButton;
import com.project.emi.eventscape.views.MessageButton;


public class UserViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = UserViewHolder.class.getSimpleName();

    private Context context;
    private ImageView photoImageView;
    private TextView nameTextView;
    private FollowButton followButton;

    private MessageButton messageButton;

    private ProfileManager profileManager;

    private Activity activity;

    public UserViewHolder(View view, final Callback callback, Activity activity) {
        super(view);
        this.context = view.getContext();
        this.activity = activity;
        profileManager = ProfileManager.getInstance(context);

        nameTextView = view.findViewById(R.id.nameTextView);
        photoImageView = view.findViewById(R.id.photoImageView);
        followButton = view.findViewById(R.id.followButton);
        messageButton = view.findViewById(R.id.messageButton);

        view.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (callback != null && position != RecyclerView.NO_POSITION) {
                callback.onItemClick(getAdapterPosition(), v);
            }
        });

        followButton.setOnClickListener(v -> {
            if (callback != null) {
                callback.onFollowButtonClick(getAdapterPosition(), followButton);
            }
        });

        messageButton.setOnClickListener(v->{
            if(callback!=null){
                callback.onMessageButtonClick(getAdapterPosition(), messageButton);
            }
        });
    }

    public void bindData(String profileId) {
        profileManager.getProfileSingleValue(profileId, createProfileChangeListener());
    }

    public void bindData(User profile) {
        fillInProfileFields(profile);
    }

    private OnObjectChangedListener<User> createProfileChangeListener() {
        return new OnObjectChangedListenerSimple<User>() {
            @Override
            public void onObjectChanged(User obj) {
                fillInProfileFields(obj);
            }
        };
    }

    protected void fillInProfileFields(User profile) {
        nameTextView.setText(profile.getUsername());
        followButton.setVisibility(View.VISIBLE);
        messageButton.setVisibility(View.VISIBLE);
        messageButton.setState(FollowState.NO_ONE_FOLLOW);
        followButton.setState(FollowState.NO_ONE_FOLLOW);
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId != null) {
            if (!currentUserId.equals(profile.getUser_id())) {
                FollowManager.getInstance(context).checkFollowState(currentUserId, profile.getUser_id(), followState -> {
                    followButton.setVisibility(View.VISIBLE);
                    followButton.setState(followState);
                });
            } else {
                messageButton.setState(FollowState.MY_PROFILE);
                followButton.setState(FollowState.MY_PROFILE);
            }
        } else {
            messageButton.setState(FollowState.NO_ONE_FOLLOW);
            followButton.setState(FollowState.NO_ONE_FOLLOW);

        }

        if (profile.getPhotoUrl() != null) {
            ImageUtil.loadImage(GlideApp.with(activity), profile.getPhotoUrl(), photoImageView);
        }
    }

    public interface Callback {
        void onItemClick(int position, View view);

        void onFollowButtonClick(int position, FollowButton followButton);

        void onMessageButtonClick(int position, MessageButton messageButton);
    }

}