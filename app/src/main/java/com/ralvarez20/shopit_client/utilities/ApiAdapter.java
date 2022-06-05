package com.ralvarez20.shopit_client.utilities;

import com.ralvarez20.shopit_client.interfaces.ishopit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiAdapter {
    private static ishopit iShopit;

    public static ishopit getApiService(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Asociamos el interceptor a las peticiones
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(80, TimeUnit.SECONDS)
                .connectTimeout(80, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);



        String baseUrl = "https://azure-shopit.azurewebsites.net/api/shopit/";

        if (iShopit == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build()) // <-- usamos el log level
                    .build();
            iShopit = retrofit.create(ishopit.class);
        }

        return iShopit;
    }

}
