
package com.project.emi.eventscape.domain.home;

import android.view.View;

import com.project.emi.eventscape.domain.base.BaseFragmentView;
import com.project.emi.eventscape.models.Post;

public interface HomeView extends BaseFragmentView {
    void openCreatePostActivity();
    void hideCounterView();
    void openPostDetailsActivity(Post post, View v);
    void showFloatButtonRelatedSnackBar(int messageId);
    void openProfileActivity(String userId, View view);
    void refreshPostList();
    void removePost();
    void updatePost();
    void showCounterView(int count);
}
