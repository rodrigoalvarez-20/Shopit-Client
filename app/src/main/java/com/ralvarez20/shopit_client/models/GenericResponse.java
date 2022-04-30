package com.ralvarez20.shopit_client.models;

import com.google.gson.annotations.SerializedName;

public class GenericResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("error")
    private String error;
    @SerializedName("token")
    private String token;

    public GenericResponse(String message, String error, String token) {
        this.message = message;
        this.error = error;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
