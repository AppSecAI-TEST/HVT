package com.hvt.hbapplication.ui.bookmark;

import android.support.v7.util.DiffUtil;

import com.hvt.hbapplication.data.model.FolkBookmark;
import com.hvt.hbapplication.ui.BaseView;

import java.util.List;


public interface BookmarkView extends BaseView {
    void displayFolksBookmarked(List<FolkBookmark> folkBookmarks, DiffUtil.DiffResult diffResult);

    void navigateToDetailFolkByID(int id);

    void rollbackItemError(int position);

    void showEmptyText(boolean show);
}
