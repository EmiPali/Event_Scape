
package com.project.emi.eventscape.domain.usersList;

import androidx.annotation.StringRes;

import com.project.emi.eventscape.domain.base.BaseView;

import java.util.List;

public interface UsersListView extends BaseView {

    void onProfilesIdsListLoaded(List<String> list);

    void showLocalProgress();

    void hideLocalProgress();

    void setTitle(@StringRes int title);

    void showEmptyListMessage(String message);

    void hideEmptyListMessage();

    void updateSelectedItem();
}
