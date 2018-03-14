package com.goldencrow.android.bakingtime.utils;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Static content for network and API usages (Retrofit).
 *
 * @author Philipp Hermüller
 * @version 2018.3.14
 * @since 1.0
 */
public class NetworkUtil {

    /**
     * Creates and returns a new Retrofit object pointing the the Base-Address.
     *
     * @return  the Retrofit object with the Base-Address.
     */
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

    /**
     * Code obtained and altered to Java-code from C#-Code from here:
     * https://stackoverflow.com/a/45285135
     * <p>
     * Creates an Interceptor which has as Header the Content-Type for the Charset UTF-8.
     */
    private static class ResponseInterceptor implements Interceptor
    {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return response.newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();
        }
    }

}
