package com.ralvarez20.shopit_client.interfaces;

import com.ralvarez20.shopit_client.models.GenericResponse;
import com.ralvarez20.shopit_client.models.Product;
import com.ralvarez20.shopit_client.models.Purchase;
import com.ralvarez20.shopit_client.models.User;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ishopit {

    @POST("users/login")
    Call<GenericResponse> loginUser(@Body RequestBody body);

    @POST("users/register")
    Call<GenericResponse> registerUser(@Body RequestBody body);

    @GET("products")
    Call<ArrayList<Product>> getProducts(@Header("Authorization") String tk, @Query("value") String name);

    @POST("products/add")
    Call<GenericResponse> addNewProduct(@Header("Authorization") String tk, @Body Product prod);

    @GET("users/me")
    Call<User> getProfile(@Header("Authorization") String tk);

    /*@GET("users/me/purchases")
    Call<ArrayList<Purchase>> getPurchases(@Header("Authorization") String tk);

    @GET("users/me/purchases/products")
    Call<ArrayList<Product>> getProductsPurchase(@Header("Authorization") String tk, @Query("purchase") int id_purchase);*/

    @POST("purchases")
    Call<GenericResponse> makePurchase(@Header("Authorization") String tk, @Body ArrayList<Product> products);

}
