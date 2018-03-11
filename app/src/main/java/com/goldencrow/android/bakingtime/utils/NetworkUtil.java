package com.goldencrow.android.bakingtime.utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Philipp
 */

public class NetworkUtil {

    public static Retrofit getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ResponseInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


    private static class ResponseInterceptor implements Interceptor
    {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return response.newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();
        }
    }

}
