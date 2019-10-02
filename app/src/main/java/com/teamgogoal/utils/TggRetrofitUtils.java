package com.teamgogoal.utils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TggRetrofitUtils {

    private static Retrofit tggRetrofit;

    private static String token = "";

    private static final String BACKEND_URL = "https://teamgogoal.herokuapp.com/";

    static {
        tggRetrofit = new Retrofit.Builder()
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
