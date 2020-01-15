
package com.project.emi.eventscape.domain.newprofile;

import android.text.Spannable;
import android.view.View;

import com.project.emi.eventscape.domain.base.BaseView;
import com.project.emi.eventscape.enums.FollowState;
import com.project.emi.eventscape.models.Post;
import com.project.emi.eventscape.models.User;


public interface ProfileView extends BaseView {

    void showUnfollowConfirmation(User profile);

    void updateFollowButtonState(FollowState followState);

    void updateFollowersCount(int count);

    void updateFollowingsCount(int count);

    void setFollowStateChangeResultOk();

    void openPostDetailsActivity(Post post, View postItemView);

    void startEditProfileActivity();

    void openCreatePostActivity();

    void setProfileName(String username);

    void setProfilePhoto(String photoUrl);

    void setDefaultProfilePhoto();

    void updateLikesCounter(Spannable text);

    void hideLoadingPostsProgress();

    void showLikeCounter(boolean show);

    void updatePostsCounter(Spannable text);

    void showPostCounter(boolean show);

    void onPostRemoved();

    void onPostUpdated();

}
