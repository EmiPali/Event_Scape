package com.project.emi.eventscape.adapters.base;

import androidx.recyclerview.widget.RecyclerView;

import com.project.emi.eventscape.core.managers.PostManager;
import com.project.emi.eventscape.core.managers.listeners.OnPostChangedListener;
import com.project.emi.eventscape.domain.base.BaseFragment;
import com.project.emi.eventscape.models.Post;
import com.project.emi.eventscape.util.LogUtil;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseFragmentPostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = BasePostsAdapter.class.getSimpleName();

    protected List<Post> postList = new LinkedList<>();
    protected BaseFragment fragment;
    protected int selectedPostPosition = RecyclerView.NO_POSITION;

    public BaseFragmentPostsAdapter(BaseFragment fragment) {
        this.fragment = fragment;
    }

    protected void cleanSelectedPostInformation() {
        selectedPostPosition = -1;
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position).getItemType().getTypeCode();
    }

    protected Post getItemByPosition(int position) {
        return postList.get(position);
    }

    private OnPostChangedListener createOnPostChangeListener(final int postPosition) {
        return new OnPostChangedListener() {
            @Override
            public void onObjectChanged(Post obj) {
                postList.set(postPosition, obj);
                notifyItemChanged(postPosition);
            }

            @Override
            public void onError(String errorText) {
                LogUtil.logDebug(TAG, errorText);
            }
        };
    }

    public void updateSelectedPost() {
        if (selectedPostPosition != RecyclerView.NO_POSITION) {
            Post selectedPost = getItemByPosition(selectedPostPosition);
            PostManager.getInstance(fragment.getContext()).getSinglePostValue(selectedPost.getId(), createOnPostChangeListener(selectedPostPosition));
        }
    }
}
