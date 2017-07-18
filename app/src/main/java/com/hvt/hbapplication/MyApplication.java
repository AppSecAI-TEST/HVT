package com.hvt.hbapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.hvt.hbapplication.network.ApiClient;
import com.hvt.hbapplication.util.LocaleHelper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyApplication extends Application {

    private static MyApplication application;

    private ApiClient apiClient;

    public SharedPreferences sharedPref;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        application = this;
        createApiClient();
    }

    public static MyApplication getApplication() {
        return application;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    private void createApiClient() {
        if (apiClient != null) return;

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("APP_API_KEY", Constant.APP_API_KEY)
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiClient = retrofit.create(ApiClient.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(base);
        super.attachBaseContext(LocaleHelper.onAttach(base, sharedPref.getString(Constant.LANG, Constant.EN)));
    }
}
