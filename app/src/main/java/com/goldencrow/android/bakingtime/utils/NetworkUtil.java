package com.goldencrow.android.bakingtime.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Philipp
 */

public class NetworkUtil {

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().build();


        return new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

}
