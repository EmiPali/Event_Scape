
package com.project.emi.eventscape.core.managers.listeners;

import com.project.emi.eventscape.models.PostListResult;

public interface OnPostListChangedListener<Post> {

    public void onListChanged(PostListResult result);

    void onCanceled(String message);
}
