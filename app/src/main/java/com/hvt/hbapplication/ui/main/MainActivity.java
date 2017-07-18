package com.hvt.hbapplication.ui.main;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hvt.hbapplication.R;
import com.hvt.hbapplication.ui.BaseActivity;
import com.hvt.hbapplication.ui.bookmark.BookmarkFragment;
import com.hvt.hbapplication.ui.home.HomeFragment;
import com.hvt.hbapplication.ui.search.SearchFragment;
import com.hvt.hbapplication.ui.settings.SettingsFragment;
import com.roughike.bottombar.BottomBar;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;


    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;

    @BindView(R.id.searchView)
    SearchView searchView;

    PublishSubject<String> searchObservable;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    HomeFragment homeFragment;
    SearchFragment searchFragment;
    BookmarkFragment bookmarkFragment;
    SettingsFragment settingsFragment;

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.tab_home));
        bottomBar.setOnTabSelectListener(this::onTabReSelected);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void initData() {
        searchObservable = PublishSubject.create();
        Disposable disposable = searchObservable.debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textQuery -> searchFragment.queryFolks(textQuery));
        compositeDisposable.add(disposable);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_main;
    }

    public void onTabReSelected(int tabId) {
        switch (tabId) {
            case R.id.tab_home:
                changeTab(0);
                toolbar.setTitle(getString(R.string.tab_home));
                changeVisibilitySearchView(false);
                break;
            case R.id.tab_search:
                changeTab(1);
                toolbar.setTitle(getString(R.string.tab_search));
                changeVisibilitySearchView(true);
                break;
            case R.id.tab_bookmark:
                changeTab(2);
                toolbar.setTitle(getString(R.string.tab_bookmark));
                changeVisibilitySearchView(false);
                break;
            case R.id.tab_settings:
                changeTab(3);
                toolbar.setTitle(getString(R.string.tab_setting));
                changeVisibilitySearchView(false);
                break;
        }
    }

    private void changeVisibilitySearchView(boolean isVisibility) {
        if (isVisibility) {
            searchView.setVisibility(View.VISIBLE);
        } else {
            searchView.setVisibility(View.GONE);
        }
    }

    private void changeTab(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                if (homeFragment == null) homeFragment = new HomeFragment();
                fragment = homeFragment;
                break;
            case 1:
                if (searchFragment == null) searchFragment = new SearchFragment();
                fragment = searchFragment;
                break;
            case 2:
                if (bookmarkFragment == null) bookmarkFragment = new BookmarkFragment();
                fragment = bookmarkFragment;
                break;
            case 3:
                if (settingsFragment == null) settingsFragment = new SettingsFragment();
                fragment = settingsFragment;
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchObservable.onNext(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchObservable.onNext(newText);
        return true;
    }

    public void updateViewLanguage(String newLang) {
//        homeFragment.updateViewLanguage(newLang);
    }

    @Override
    public void onAttachView() {

    }

    @Override
    public void onDetachView() {
        compositeDisposable.clear();
    }
}
