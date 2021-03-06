package com.project.emi.eventscape.domain.post.createPost;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.domain.post.BaseCreatePostPresenter;
import com.project.emi.eventscape.enums.EventType;
import com.project.emi.eventscape.enums.ItemType;
import com.project.emi.eventscape.models.Post;

import java.util.Objects;

public class CreatePostPresenter extends BaseCreatePostPresenter<CreatePostView> {

    public CreatePostPresenter(Context context) {
        super(context);
    }

    @Override
    protected int getSaveFailMessage() {
        return R.string.error_fail_create_post;
    }

    @Override
    protected void savePost(String title, String description, EventType eventType, ItemType itemType, String text) {
        ifViewAttached(view -> {
            view.showProgress(R.string.message_creating_post);
            Post post = new Post();
            post.setTitle(title);
            post.setDescription(description);
            post.setAuthorId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            post.setItemType(itemType);
            post.setEventType(eventType);
            if(text!=null)
            post.setTextContent(text);
            postManager.createOrUpdatePostWithImage(view.getImageUri(), this, post);
        });
    }

    @Override
    protected boolean isImageRequired() {
        return false;
    }
}
