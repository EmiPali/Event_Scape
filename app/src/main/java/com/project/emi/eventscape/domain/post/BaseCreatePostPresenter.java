
package com.project.emi.eventscape.domain.post;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.StringRes;

import com.project.emi.eventscape.R;
import com.project.emi.eventscape.core.managers.PostManager;
import com.project.emi.eventscape.core.managers.listeners.OnPostCreatedListener;
import com.project.emi.eventscape.domain.pickImageBase.PickImagePresenter;
import com.project.emi.eventscape.enums.EventType;
import com.project.emi.eventscape.enums.ItemType;
import com.project.emi.eventscape.util.LogUtil;
import com.project.emi.eventscape.util.ValidationUtil;


public abstract class BaseCreatePostPresenter<V extends BaseCreatePostView> extends PickImagePresenter<V> implements OnPostCreatedListener {

    protected boolean creatingPost = false;
    protected PostManager postManager;

    public BaseCreatePostPresenter(Context context) {
        super(context);
        postManager = PostManager.getInstance(context);
    }

    @StringRes
    protected abstract int getSaveFailMessage();

    protected abstract void savePost(final String title, final String description, EventType eventType, ItemType itemType, String content);

    protected abstract boolean isImageRequired();

    protected void attemptCreatePost(Uri imageUri,EventType eventType, ItemType itemType, String content) {
        // Reset errors.
        ifViewAttached(view -> {
            view.setTitleError(null);
            view.setDescriptionError(null);

            String title = view.getTitleText().trim();
            String description = view.getDescriptionText().trim();

            boolean cancel = false;

            if (TextUtils.isEmpty(description)) {
                view.setDescriptionError(context.getString(R.string.warning_empty_description));
                cancel = true;
            }

            if (TextUtils.isEmpty(title)) {
                view.setTitleError(context.getString(R.string.warning_empty_title));
                cancel = true;
            } else if (!ValidationUtil.isPostTitleValid(title)) {
                view.setTitleError(context.getString(R.string.error_post_title_length));
                cancel = true;
            }

            if (isImageRequired() && view.getImageUri() == null) {
                view.showWarningDialog(R.string.warning_empty_image);
                view.requestImageViewFocus();
                cancel = true;
            }

            if (!cancel) {
                creatingPost = true;
                view.hideKeyboard();
                savePost(title, description, eventType, itemType, content);
            }
        });
    }

    public void doSavePost(Uri imageUri, EventType eventType,ItemType itemType, String content) {
        if (!creatingPost) {
            if (hasInternetConnection()) {
                attemptCreatePost(imageUri, eventType ,itemType, content);
            } else {
                ifViewAttached(view -> view.showSnackBar(R.string.internet_connection_failed));
            }
        }
    }

    @Override
    public void onPostSaved(boolean success) {
        creatingPost = false;

        ifViewAttached(view -> {
            view.hideProgress();
            if (success) {
                view.onPostSavedSuccess();
                LogUtil.logDebug(TAG, "Post was saved");
            } else {
                view.showSnackBar(getSaveFailMessage());
                LogUtil.logDebug(TAG, "Failed to save a post");
            }
        });
    }

}
