package com.ralvarez20.shopit_client.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Purchase {
    @SerializedName("no_items")
    private int total_products;
    @SerializedName("total")
    private double total;
    @SerializedName("products")
    private ArrayList<Product> products;
    @SerializedName("date")
    private String date;

    public Purchase(int total_products, double total, ArrayList<Product> products, String date) {
        this.total_products = total_products;
        this.total = total;
        this.products = products;
        this.date = date;
    }

    public int getTotal_products() {
        return total_products;
    }

    public void setTotal_products(int total_products) {
        this.total_products = total_products;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
