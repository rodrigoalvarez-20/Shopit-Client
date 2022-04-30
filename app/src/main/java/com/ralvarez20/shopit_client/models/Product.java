package com.ralvarez20.shopit_client.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Product {
    private int id;
    private String image;
    private String sku;
    private String name;
    private String description;
    private String category;
    private int stock;
    private int quantity;
    private double price;
    //private Date createdAt;

    public Product(int id, String image, String sku, String name, String description, String category, int stock, int quantity, double price) {
        this.id = id;
        this.image = image;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.description = description;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        //this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
