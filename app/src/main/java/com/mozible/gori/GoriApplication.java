package com.mozible.gori;

import android.app.Application;

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

    @Override
    public void onCreate() {
        super.onCreate();
        GoriApplication.instance = this;
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
}
