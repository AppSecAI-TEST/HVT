package com.hvt.hbapplication.ui.bookmark;

import android.support.v7.util.DiffUtil;
import android.util.Pair;

import com.hvt.hbapplication.R;
import com.hvt.hbapplication.data.model.FolkBookmark;
import com.hvt.hbapplication.network.ApiClient;
import com.hvt.hbapplication.ui.BasePresenter;
import com.hvt.hbapplication.util.recyclerview.BookmarkDiffUtil;

import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class BookmarkPresenter extends BasePresenter<BookmarkView> {

    private List<FolkBookmark> folksBookmarked;

    private PublishSubject<List<FolkBookmark>> observeChange;


    public BookmarkPresenter(ApiClient apiClient) {
        super(apiClient);
        createObserveDataChange();
    }

    private void createObserveDataChange() {
        observeChange = PublishSubject.create();
        observeChange.scan(new Pair<List<FolkBookmark>, DiffUtil.DiffResult>(Collections.emptyList(), null),
                (oldPair, newestData) -> {
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BookmarkDiffUtil(oldPair.first, newestData));
                    return Pair.create(newestData, diffResult);
                })
                .skip(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lastPair -> {
                    folksBookmarked = lastPair.first;
                    getView().showEmptyText(lastPair.first == null || lastPair.first.isEmpty());
                    getView().displayFolksBookmarked(lastPair.first, lastPair.second);
                }, throwable -> getView().showError(R.string.bookmark_load_error));
    }

    public void loadFolksBookmarked() {
        dataManager.getFolksBookmarked(observeChange);
    }

    public void prepareForNavigateToDetailFolk(int position) {
        if (folksBookmarked != null && position < folksBookmarked.size()) {
            getView().navigateToDetailFolkByID(folksBookmarked.get(position).idFolk);
        }
    }

    public void updateFolkBookmarkSaveChange(int position) {
        if (folksBookmarked != null && position < folksBookmarked.size()) {
            FolkBookmark folkBookmarkChange = folksBookmarked.get(position);
            boolean oldState = folkBookmarkChange.isSelected;
            boolean newState = !oldState;
            folkBookmarkChange.isSelected = newState;

            Disposable disposable;
            if (newState) {
                disposable = dataManager.bookmarkFolk(folkBookmarkChange.idFolk, folkBookmarkChange.backgroundUrl, folkBookmarkChange.name).subscribe(result -> {
                    //do nothing
                }, throwable -> {
                    getView().showError(R.string.detail_save_failure);
                    folkBookmarkChange.isSelected = oldState;
                    getView().rollbackItemError(position);
                });
            } else {
                disposable = dataManager.unBookmarkFolk(folkBookmarkChange.idFolk).subscribe(result -> {
                    //do nothing
                }, throwable -> {
                    getView().showError(R.string.detail_unsave_failure);
                    folkBookmarkChange.isSelected = oldState;
                    getView().rollbackItemError(position);
                });
            }


            compositeDisposable.add(disposable);
        }
    }
}
