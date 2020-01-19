package com.project.emi.eventscape.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.emi.eventscape.R;
import com.project.emi.eventscape.adapters.base.BasePostsAdapter;
import com.project.emi.eventscape.adapters.holders.LoadViewHolder;
import com.project.emi.eventscape.adapters.holders.PostViewHolder;
import com.project.emi.eventscape.adapters.holders.TextPostViewHolder;
import com.project.emi.eventscape.controllers.LikeController;
import com.project.emi.eventscape.domain.base.BaseActivity;
import com.project.emi.eventscape.enums.ItemType;
import com.project.emi.eventscape.models.Post;

import java.util.List;


public class SearchPostsAdapter extends BasePostsAdapter {
    public static final String TAG = SearchPostsAdapter.class.getSimpleName();

    private CallBack callBack;

    public SearchPostsAdapter(final BaseActivity activity) {
        super(activity);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ItemType.ITEM.getTypeCode()) {
            View view = inflater.inflate(R.layout.post_item_list_view, parent, false);
            return new PostViewHolder(view, createOnClickListener(), activity, true);
        } else if (viewType == ItemType.TEXT.getTypeCode()) {
            return new TextPostViewHolder(inflater.inflate(R.layout.text_post_item_list_view, parent, false), createOnClickListener(),activity);
        } else {
            return new LoadViewHolder(inflater.inflate(R.layout.loading_view, parent, false));
        }
    }

    private PostViewHolder.OnClickListener createOnClickListener() {
        return new PostViewHolder.OnClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (callBack != null && callBack.enableClick()) {
                    selectedPostPosition = position;
                    callBack.onItemClick(getItemByPosition(position), view);
                }
            }

            @Override
            public void onLikeClick(LikeController likeController, int position) {
                if (callBack != null && callBack.enableClick()) {
                    Post post = getItemByPosition(position);
                    likeController.handleLikeClickAction(activity, post);
                }
            }

            @Override
            public void onAuthorClick(int position, View view) {
                if (callBack != null && callBack.enableClick()) {
                    callBack.onAuthorClick(getItemByPosition(position).getAuthorId(), view);
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ItemType.ITEM.getTypeCode()) {
            ((PostViewHolder) holder).bindData(postList.get(position));
        } else if (getItemViewType(position) == ItemType.TEXT.getTypeCode()) {
            ((TextPostViewHolder) holder).bindData(postList.get(position));
        }
    }

    public void setList(List<Post> list) {
        cleanSelectedPostInformation();
        postList.clear();
        postList.addAll(list);
        notifyDataSetChanged();
    }

    public void removeSelectedPost() {
        if (selectedPostPosition != RecyclerView.NO_POSITION) {
            postList.remove(selectedPostPosition);
            notifyItemRemoved(selectedPostPosition);
        }
    }

    public interface CallBack {
        void onItemClick(Post post, View view);

        void onAuthorClick(String authorId, View view);

        boolean enableClick();
    }
}
