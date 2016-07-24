package com.mozible.gori;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mozible.gori.utils.GoriConstants;
import com.mozible.gori.utils.ServerInterface;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by JunLee on 7/22/16.
 */
public class GoriApplication extends Application {

    public static GoriApplication instance;

    private ServerInterface api;

    private String endPoint;
    private static final String PROPERTY_ID = "GA_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        GoriApplication.instance = this;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    public void buildServerinterface() {
        if(api == null) {
            endPoint = GoriConstants.BASE_URL;
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);
            okHttpClient.networkInterceptors().add(mCacheControlInterceptor);
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setConverter(new GsonConverter(gson));
            builder.setEndpoint(endPoint);
            builder.setClient(new OkClient(okHttpClient));
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {

                }
            });
            RestAdapter adapter = builder.build();
            api = adapter.create(ServerInterface.class);
        }
    }
    private static final Interceptor mCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            // Add Cache Control only for GET methods
            if (request.method().equals("GET")) {
                    request.newBuilder()
                            .header("Cache-Control", "no-cache, no-store, must-revalidate")
                            .build();
            }

            Response response = chain.proceed(request);

            // Re-write response CC header to force use of cache
            return response.newBuilder()
                    .header("Cache-Control", "no-cache, no-store, must-revalidate") // 1 day
                    .build();
        }
    };

    public static GoriApplication getInstance() {
        return instance;
    }

    public ServerInterface getServerInterface() {
        return api;
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
        // roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
        // company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
                    .newTracker(R.xml.global_tracker) : analytics
                    .newTracker(R.xml.ecommerce_tracker);

            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
