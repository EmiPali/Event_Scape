package com.project.emi.eventscape.domain.search.users;

import com.project.emi.eventscape.domain.base.BaseFragmentView;
import com.project.emi.eventscape.models.User;


import java.util.List;

public interface SearchUsersView extends BaseFragmentView {
    void onSearchResultsReady(List<User> profiles);

    void showLocalProgress();

    void hideLocalProgress();

    void showEmptyListLayout();

    void updateSelectedItem();
}
