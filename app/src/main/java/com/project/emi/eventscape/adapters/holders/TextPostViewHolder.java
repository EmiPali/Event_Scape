package com.project.emi.eventscape.adapters.holders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.controllers.LikeController;
import com.project.emi.eventscape.core.Constants;
import com.project.emi.eventscape.core.managers.PostManager;
import com.project.emi.eventscape.core.managers.ProfileManager;
import com.project.emi.eventscape.core.managers.listeners.OnObjectChangedListener;
import com.project.emi.eventscape.core.managers.listeners.OnObjectChangedListenerSimple;
import com.project.emi.eventscape.core.managers.listeners.OnObjectExistListener;
import com.project.emi.eventscape.domain.base.BaseActivity;
import com.project.emi.eventscape.models.Like;
import com.project.emi.eventscape.models.Post;
import com.project.emi.eventscape.models.User;
import com.project.emi.eventscape.util.FormatterUtil;
import com.project.emi.eventscape.util.GlideApp;
import com.project.emi.eventscape.util.ImageUtil;

public class TextPostViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = TextPostViewHolder.class.getSimpleName();

    protected Context context;
    private TextView post_text;
    private TextView titleTextView;
    private TextView detailsTextView;
    private TextView likeCounterTextView;
    private ImageView likesImageView;
    private TextView commentsCountTextView;
    private TextView watcherCounterTextView;
    private TextView dateTextView;
    private ImageView authorImageView;
    private ViewGroup likeViewGroup;

    private ProfileManager profileManager;
    protected PostManager postManager;

    private LikeController likeController;
    private BaseActivity baseActivity;

    public TextPostViewHolder(View view, final PostViewHolder.OnClickListener onClickListener, BaseActivity activity) {
        this(view, onClickListener, activity, true);
    }

    public TextPostViewHolder(View view, final PostViewHolder.OnClickListener onClickListener, BaseActivity activity, boolean isAuthorNeeded) {
        super(view);
        this.context = view.getContext();
        this.baseActivity = activity;

        post_text = view.findViewById(R.id.post_text);
        likeCounterTextView = view.findViewById(R.id.likeCounterTextView);
        likesImageView = view.findViewById(R.id.likesImageView);
        commentsCountTextView = view.findViewById(R.id.commentsCountTextView);
        watcherCounterTextView = view.findViewById(R.id.watcherCounterTextView);
        dateTextView = view.findViewById(R.id.dateTextView);
        authorImageView = view.findViewById(R.id.authorImageView);
        likeViewGroup = view.findViewById(R.id.likesContainer);

        authorImageView.setVisibility(isAuthorNeeded ? View.VISIBLE : View.GONE);

        profileManager = ProfileManager.getInstance(context.getApplicationContext());
        postManager = PostManager.getInstance(context.getApplicationContext());

        view.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onItemClick(getAdapterPosition(), v);
            }
        });

        likeViewGroup.setOnClickListener(view1 -> {
            int position = getAdapterPosition();
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onLikeClick(likeController, position);
            }
        });

        authorImageView.setOnClickListener(v -> {
            int position = getAdapterPosition();
            if (onClickListener != null && position != RecyclerView.NO_POSITION) {
                onClickListener.onAuthorClick(getAdapterPosition(), v);
            }
        });
    }

    public void bindData(Post post) {

        likeController = new LikeController(context, post, likeCounterTextView, likesImageView, true);

        String title = removeNewLinesDividers(post.getTitle());
        String description = removeNewLinesDividers(post.getDescription());

        likeCounterTextView.setText(String.valueOf(post.getLikesCount()));
        commentsCountTextView.setText(String.valueOf(post.getCommentsCount()));
        watcherCounterTextView.setText(String.valueOf(post.getWatchersCount()));

        post_text.setText(post.getTextContent());
        CharSequence date = FormatterUtil.getRelativeTimeSpanStringShort(context, post.getCreatedDate());
        dateTextView.setText(date);


        if (post.getAuthorId() != null) {
            profileManager.getProfileSingleValue(post.getAuthorId(), createProfileChangeListener(authorImageView));
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            postManager.hasCurrentUserLikeSingleValue(post.getId(), firebaseUser.getUid(), createOnLikeObjectExistListener());
        }
    }

    private String removeNewLinesDividers(String text) {
        int decoratedTextLength = text.length() < Constants.Post.MAX_TEXT_LENGTH_IN_LIST ?
                text.length() : Constants.Post.MAX_TEXT_LENGTH_IN_LIST;
        return text.substring(0, decoratedTextLength).replaceAll("\n", " ").trim();
    }

    private OnObjectChangedListener<User> createProfileChangeListener(final ImageView authorImageView) {
        return new OnObjectChangedListenerSimple<User>() {
            @Override
            public void onObjectChanged(User obj) {
                if (obj != null && obj.getPhotoUrl() != null) {
                    if (!baseActivity.isFinishing() && !baseActivity.isDestroyed()) {
                        ImageUtil.loadImage(GlideApp.with(baseActivity), obj.getPhotoUrl(), authorImageView);
                    }
                }
            }
        };
    }

    private OnObjectExistListener<Like> createOnLikeObjectExistListener() {
        return exist -> likeController.initLike(exist);
    }

    public interface OnClickListener {
        void onItemClick(int position, View view);

        void onLikeClick(LikeController likeController, int position);

        void onAuthorClick(int position, View view);
    }
}