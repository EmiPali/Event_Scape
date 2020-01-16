package com.project.emi.eventscape.domain.pickImageBase;

import android.net.Uri;

import com.project.emi.eventscape.domain.base.BaseView;

public interface PickImageView extends BaseView {
    void hideLocalProgress();

    void loadImageToImageView(Uri imageUri);
}
