/*
 * Copyright 2017 Rozdoum
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

package com.project.emi.eventscape.domain.pickImageBase;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.stream.MediaStoreVideoThumbLoader;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.greentoad.turtlebody.mediapicker.MediaPicker;
import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.core.Constants;
import com.project.emi.eventscape.domain.base.BaseActivity;
import com.project.emi.eventscape.enums.EventType;
import com.project.emi.eventscape.util.GlideApp;
import com.project.emi.eventscape.util.ImageUtil;
import com.project.emi.eventscape.util.LogUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class PickImageActivity<V extends PickImageView, P extends PickImagePresenter<V>> extends BaseActivity<V, P> implements PickImageView {
    private static final String SAVED_STATE_IMAGE_URI = "RegistrationActivity.SAVED_STATE_IMAGE_URI";

    private static final int REQUEST_CODE_CHOOSE = 23;

    protected Uri imageUri;

    protected abstract ProgressBar getProgressView();

    protected abstract ImageView getImageView();

    protected abstract void onImagePikedAction(EventType eventType);

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVED_STATE_IMAGE_URI, imageUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_STATE_IMAGE_URI)) {
                imageUri = savedInstanceState.getParcelable(SAVED_STATE_IMAGE_URI);
                loadImageToImageView(imageUri);
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressLint("NewApi")
    public void onSelectImageClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            showAlert();
        }
    }

    private void showAlert(){

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_view)
                .create();

        alertDialog.show();
        Button pictureButton = (Button) alertDialog.findViewById(R.id.activity_picture_select);

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startMediaPicker(MediaPicker.MediaTypes.IMAGE,false);
            }
        });

        Button videoButton = (Button) alertDialog.findViewById(R.id.activity_videoo_select);

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startMediaPicker(MediaPicker.MediaTypes.VIDEO,false);
            }
        });

    }

    public void startMediaPicker(int filetype, boolean multi){
        MediaPickerConfig pickerConfig = new MediaPickerConfig()
                .setAllowMultiSelection(false)
                .setUriPermanentAccess(true)
                .setShowConfirmationDialog(true)
                .setAllowMultiSelection(multi).setShowConfirmationDialog(true)
                .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MediaPicker.with(Objects.requireNonNull(this),filetype)
                .setConfig(pickerConfig)
                .onResult()
                .subscribe(new Observer<ArrayList<Uri>>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onNext(ArrayList<Uri> uris) {
                        if (presenter.isImageFileValid(uris.get(0))) {
                            imageUri = uris.get(0);
                        }

                        // For API >= 23 we need to check specifically that we have permissions to read external storage.
                        if (CropImage.isReadExternalStoragePermissionsRequired(PickImageActivity.this, imageUri)) {
                            // request permissions and handle the result in onRequestPermissionsResult()
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                        } else {
                            // no permissions required or already grunted
                            onImagePikedAction(filetype == MediaPicker.MediaTypes.VIDEO? EventType.VIDEO: EventType.PHOTO);
                        }
                    }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onComplete() { }
                });
    }

    @Override
    public void loadImageToImageView(Uri imageUri) {
        if (imageUri == null) {
            return;
        }

        this.imageUri = imageUri;
        ImageUtil.loadLocalImage(GlideApp.with(this), imageUri, getImageView(), new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                getProgressView().setVisibility(View.GONE);
                LogUtil.logDebug(TAG, "Glide Success Loading image from uri : " + imageUri.getPath());
                return false;
            }
        });
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            if (presenter.isImageFileValid(imageUri)) {
                this.imageUri = imageUri;
            }

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted
                onImagePikedAction(EventType.PHOTO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtil.logDebug(TAG, "CAMERA_CAPTURE_PERMISSIONS granted");
                CropImage.startPickImageActivity(this);
            } else {
                showSnackBar(R.string.permissions_not_granted);
                LogUtil.logDebug(TAG, "CAMERA_CAPTURE_PERMISSIONS not granted");
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (imageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LogUtil.logDebug(TAG, "PICK_IMAGE_PERMISSIONS granted");
                onImagePikedAction(EventType.PHOTO);
            } else {
                showSnackBar(R.string.permissions_not_granted);
                LogUtil.logDebug(TAG, "PICK_IMAGE_PERMISSIONS not granted");
            }
        }
    }

    protected void handleCropImageResult(int requestCode, int resultCode, Intent data) {
        presenter.handleCropImageResult(requestCode, resultCode, data);
    }

    protected void startCropImageActivity() {
        if (imageUri == null) {
            return;
        }

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setMinCropResultSize(Constants.Profile.MIN_AVATAR_SIZE, Constants.Profile.MIN_AVATAR_SIZE)
                .setRequestedSize(Constants.Profile.MAX_AVATAR_SIZE, Constants.Profile.MAX_AVATAR_SIZE)
                .start(this);
    }

    @Override
    public void hideLocalProgress() {
        getProgressView().setVisibility(View.GONE);
    }
}

