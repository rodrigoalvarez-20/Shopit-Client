package com.ralvarez20.shopit_client.models;

public class Purchase {
    private  int id, id_usuario, total_products;
    private double total;

    public Purchase(int id, int id_usuario, int total_products, double total) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.total_products = total_products;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
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
}
