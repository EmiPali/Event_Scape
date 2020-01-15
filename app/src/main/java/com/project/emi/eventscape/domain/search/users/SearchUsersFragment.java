package com.project.emi.eventscape.domain.search.users;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.project.emi.eventscape.R;
import com.project.emi.eventscape.adapters.SearchUsersAdapter;
import com.project.emi.eventscape.adapters.holders.UserViewHolder;
import com.project.emi.eventscape.domain.base.BaseFragment;
import com.project.emi.eventscape.domain.newprofile.NewProfileActivity;
import com.project.emi.eventscape.domain.profile.ProfileActivity;
import com.project.emi.eventscape.domain.search.Searchable;
import com.project.emi.eventscape.models.User;
import com.project.emi.eventscape.util.AnimationUtils;
import com.project.emi.eventscape.views.FollowButton;

import java.util.List;

public class SearchUsersFragment extends BaseFragment<SearchUsersView, SearchUsersPresenter> implements SearchUsersView, Searchable {

    private static final String TAG = "SearchUsersFragment";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SearchUsersAdapter usersAdapter;
    private TextView emptyListMessageTextView;
    private String lastSearchText = "";

    private boolean searchInProgress = false;

    private int selectedItemPosition = RecyclerView.NO_POSITION;


    public static final String USER_ID_EXTRA_KEY = "UsersListActivity.USER_ID_EXTRA_KEY";
    public static final String USER_LIST_TYPE = "UsersListActivity.USER_LIST_TYPE";

    public static final int UPDATE_FOLLOWING_STATE_REQ = 1501;
    public static final int UPDATE_FOLLOWING_STATE_RESULT_OK = 1502;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyListMessageTextView = view.findViewById(R.id.emptyListMessageTextView);
        emptyListMessageTextView.setText(getResources().getString(R.string.empty_user_search_message));

        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        usersAdapter = new SearchUsersAdapter(getActivity());
        usersAdapter.setCallback(new UserViewHolder.Callback() {
            @Override
            public void onItemClick(int position, View view) {
                if (!searchInProgress) {
                    selectedItemPosition = position;
                    User profile = usersAdapter.getItemByPosition(position);
                    openProfileActivity(profile.getUser_id(), view);
                }
            }

            @Override
            public void onFollowButtonClick(int position, FollowButton followButton) {
                if (!searchInProgress) {
                    selectedItemPosition = position;
                    User profile = usersAdapter.getItemByPosition(position);
                    presenter.onFollowButtonClick(followButton.getState(), ((User) profile).getUser_id());
                }
            }
        });

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(usersAdapter);

        presenter.loadUsersWithEmptySearch();
    }

    @SuppressLint("RestrictedApi")
    private void openProfileActivity(String userId, View view) {
        Intent intent = new Intent(getActivity(), NewProfileActivity.class);
        intent.putExtra(NewProfileActivity.USER_ID_EXTRA_KEY, userId);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

            ImageView imageView = view.findViewById(R.id.photoImageView);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(getActivity(),
                            new android.util.Pair<>(imageView, getString(R.string.post_author_image_transition_name)));
            startActivityForResult(intent, UPDATE_FOLLOWING_STATE_REQ, options.toBundle());
        } else {
            startActivityForResult(intent, UPDATE_FOLLOWING_STATE_REQ);
        }
    }

    @Override
    public SearchUsersPresenter createPresenter() {
        if (presenter == null) {
            return new SearchUsersPresenter(getActivity());
        }
        return presenter;
    }

    public void search(String searchText) {
        lastSearchText = searchText;
        presenter.search(searchText);
    }

    @Override
    public void onSearchResultsReady(List<User> profiles) {
        hideLocalProgress();
        emptyListMessageTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        usersAdapter.setList(profiles);
    }

    @Override
    public void showLocalProgress() {
        searchInProgress = true;
        AnimationUtils.showViewByScaleWithoutDelay(progressBar);
    }

    @Override
    public void hideLocalProgress() {
        searchInProgress = false;
        AnimationUtils.hideViewByScale(progressBar);
    }
    @Override
    public void showEmptyListLayout() {
        hideLocalProgress();
        recyclerView.setVisibility(View.GONE);
        emptyListMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateSelectedItem() {
        if (selectedItemPosition != RecyclerView.NO_POSITION) {
            usersAdapter.updateItem(selectedItemPosition);
        }
    }
}
