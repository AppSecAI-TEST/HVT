package com.hvt.hbapplication.ui;


import com.hvt.hbapplication.network.ApiClient;

/**
 * Created by Hado on 5/22/17.
 */

public abstract class BasePresenter<V extends BaseView> {

    protected ApiClient apiClient;

    public BasePresenter(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    private V baseView;

    public void onAttach(V view) {
        this.baseView = view;
    }

    public void onDetach() {
        baseView = null;
    }

    public V getView() {
        return baseView;
    }
}
