package com.hvt.hbapplication.ui.bookmark;


import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hvt.hbapplication.MyApplication;
import com.hvt.hbapplication.R;
import com.hvt.hbapplication.data.model.FolkBookmark;
import com.hvt.hbapplication.ui.BaseFragment;
import com.hvt.hbapplication.ui.OnClickItemListener;
import com.hvt.hbapplication.ui.bookmark.adapter.BookmarkAdapter;
import com.hvt.hbapplication.ui.detail.DetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookmarkFragment extends BaseFragment implements BookmarkView, OnClickItemListener {

    @BindView(R.id.rv_bookmark)
    public RecyclerView rvBookmark;

    @BindView(R.id.tv_empty_data)
    public TextView tvEmptyData;

    BookmarkPresenter presenter;

    BookmarkAdapter adapter = new BookmarkAdapter();

    public BookmarkFragment() {

    }

    @Override
    public void initView() {
        adapter.setItemClickListener(this);
        adapter.setBookmarkChangeListener(position -> presenter.updateFolkBookmarkSaveChange(position));
        rvBookmark.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBookmark.setAdapter(adapter);
    }

    @Override
    public void initData() {
        presenter.loadFolksBookmarked();
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_bookmark;
    }

    @Override
    public Unbinder bindingView(View view) {
        return ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (presenter == null) {
            presenter = new BookmarkPresenter(MyApplication.getApplication().getApiClient());
        }
        presenter.onAttach(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            presenter.loadFolksBookmarked();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.onDetach();
        }
    }

    @Override
    public void displayFolksBookmarked(List<FolkBookmark> folkBookmarks, DiffUtil.DiffResult diffResult) {
        adapter.setData(folkBookmarks, diffResult);
    }

    @Override
    public void navigateToDetailFolkByID(int id) {
        DetailActivity.navigate(getContext(), id);
    }

    @Override
    public void rollbackItemError(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void showEmptyText(boolean show) {
        tvEmptyData.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemClicked(int position) {
        presenter.prepareForNavigateToDetailFolk(position);
    }
}
