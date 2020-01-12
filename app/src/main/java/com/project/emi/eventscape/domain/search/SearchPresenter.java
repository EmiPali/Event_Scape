package com.project.emi.eventscape.domain.search;

import android.app.Activity;

import com.project.emi.eventscape.domain.base.BasePresenter;


class SearchPresenter extends BasePresenter<SearchBaseView> {

    private String currentUserId;
    private Activity activity;

    SearchPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void search(String query) {

    }
}
