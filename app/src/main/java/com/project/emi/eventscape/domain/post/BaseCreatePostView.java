package com.project.emi.eventscape.domain.post;

import android.net.Uri;

import com.project.emi.eventscape.domain.pickImageBase.PickImageView;

public interface BaseCreatePostView extends PickImageView {
    void setDescriptionError(String error);

    void setTitleError(String error);

    String getTitleText();

    String getDescriptionText();

    void requestImageViewFocus();

    void onPostSavedSuccess();

    Uri getImageUri();
}

