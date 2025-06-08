package com.example.pamuap;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    private static final String BASE_URL = "https://uappam.kuncipintu.my.id/";
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        
        // Add authentication interceptor
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            
            // Get the Firebase ID token
            String idToken = null;
            try {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    idToken = FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getToken();
                    Log.d(TAG, "Got Firebase ID token: " + (idToken != null ? "Token exists" : "Token is null"));
                } else {
                    Log.d(TAG, "No Firebase user logged in");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting Firebase ID token", e);
            }
            
            if (idToken != null) {
                // Add the token to the request header
                Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + idToken)
                    .method(original.method(), original.body());
                
                Request request = requestBuilder.build();
                Log.d(TAG, "Making API request to: " + request.url());
                return chain.proceed(request);
            }
            
            Log.d(TAG, "Making API request without token to: " + original.url());
            return chain.proceed(original);
        });

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }
} 