package com.teamgogoal.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TggRetrofitUtils {

    private static Retrofit tggRetrofit;

    private static String token = "";

    private static final String BACKEND_URL = "https://teamgogoal.herokuapp.com/";

    static {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .addInterceptor(chain-> {
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", token)
                            .build();
                    return chain.proceed(newRequest);
                }).build();

        tggRetrofit = new Retrofit.Builder()
                            .client(client)
                            .baseUrl(BACKEND_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 支持RxJava
                            .build();
    }

    public static Retrofit getTggRetrofit() {
        return tggRetrofit;
    }

    public static <T> T getTggService(Class<T> service) {
        return tggRetrofit.create(service);
    }

    public static void setToken(String token) {
        TggRetrofitUtils.token = token;
    }

    public static String getToken() {
        return token;
    }


}
