
package com.project.emi.eventscape.domain.search.posts;

import com.project.emi.eventscape.domain.base.BaseFragmentView;
import com.project.emi.eventscape.models.Post;

import java.util.List;

/**
 * Created by Alexey on 08.06.18.
 */
public interface SearchPostsView extends BaseFragmentView {
    void onSearchResultsReady(List<Post> posts);
    void showLocalProgress();
    void hideLocalProgress();
    void showEmptyListLayout();
}
