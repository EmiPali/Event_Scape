package com.project.emi.eventscape.domain.search;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.adapters.SectionsPagerAdapter;
import com.project.emi.eventscape.domain.base.BaseActivity;
import com.project.emi.eventscape.domain.search.posts.SearchPostsFragment;
import com.project.emi.eventscape.domain.search.users.SearchUsersFragment;
import com.project.emi.eventscape.util.BottomNavigationViewHelper;
import com.project.emi.eventscape.util.LogUtil;


public class SearchActivity extends BaseActivity<SearchBaseView, SearchPresenter> {
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = SearchActivity.this;
    private SectionsPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: started");
        setupBottomNavigationView();
        setupViewPager();
        initSearch();
    }

    @NonNull
    @Override
    public SearchPresenter createPresenter() {
        if (presenter == null) {
            return new SearchPresenter(this);
        }
        return presenter;
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        Log.d("this is null " + bottomNavigationViewEx + " or not ", "setupBottomNavigationView: setting up BottomNavigationView");
        //BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx, ACTIVITY_NUM);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    private void setupViewPager() {
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchUsersFragment());
        adapter.addFragment(new SearchPostsFragment());
        viewPager = findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_username);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_website);
    }

    private void initSearch() {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = this.findViewById(R.id.search_view);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
    }


    private void search(String searchText) {
        Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
        ((Searchable) fragment).search(searchText);
        LogUtil.logDebug(TAG, "search text: " + searchText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);


        return true;
    }
}
