package com.ralvarez20.shopit_client.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("name")
    private String name;
    @SerializedName("last_name")
    private String last_name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("gender")
    private String gender;
    @SerializedName("error")
    private String error;

    public User(String name, String lastName, String email, String phone, String gender){
        this.name = name;
        this.last_name = lastName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    public User(String name, String last_name, String email, String phone, String gender, String error) {
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.error = error;
    }

    public String getName(){
        return name;
    }

    public String getLastName(){
        return last_name;
    }

    public String getEmail(){
        return email;
    }

    public String getPhone(){
        return phone;
    }

    public String getGender(){
        return gender;
    }

    public void setName(String v){
        this.name = v;
    }

    public void setLastName(String v){
        this.last_name = v;
    }

    public void setEmail(String v){
        this.email = v;
    }

    public void setPhone(String v){
        this.phone = v;
    }

    public void setGender(String v){
        this.gender = v;
    }
}
