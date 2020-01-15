package com.project.emi.eventscape.core.managers;

import android.content.Context;

import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.core.interactors.CommentInteractor;
import com.project.emi.eventscape.core.managers.listeners.OnDataChangedListener;
import com.project.emi.eventscape.core.managers.listeners.OnTaskCompleteListener;
import com.project.emi.eventscape.models.Comment;


public class CommentManager extends FirebaseListenersManager {

    private static final String TAG = CommentManager.class.getSimpleName();
    private static CommentManager instance;

    private Context context;
    CommentInteractor commentInteractor;

    public static CommentManager getInstance(Context context) {
        if (instance == null) {
            instance = new CommentManager(context);
        }

        return instance;
    }

    private CommentManager(Context context) {
        this.context = context;
        commentInteractor = CommentInteractor.getInstance(context);
    }

    public void createOrUpdateComment(String commentText, String postId, OnTaskCompleteListener onTaskCompleteListener) {
        commentInteractor.createComment(commentText, postId, onTaskCompleteListener);
    }

    public void decrementCommentsCount(String postId, OnTaskCompleteListener onTaskCompleteListener) {
        commentInteractor.decrementCommentsCount(postId, onTaskCompleteListener);
    }

    public void getCommentsList(Context activityContext, String postId, OnDataChangedListener<Comment> onDataChangedListener) {
        ValueEventListener valueEventListener = commentInteractor.getCommentsList(postId, onDataChangedListener);
        addListenerToMap(activityContext, valueEventListener);
    }

    public void removeComment(String commentId, final String postId, final OnTaskCompleteListener onTaskCompleteListener) {
        commentInteractor.removeComment(commentId, postId, onTaskCompleteListener);
    }

    public void updateComment(String commentId, String commentText, String postId, OnTaskCompleteListener onTaskCompleteListener) {
        commentInteractor.updateComment(commentId, commentText, postId, onTaskCompleteListener);
    }
}
