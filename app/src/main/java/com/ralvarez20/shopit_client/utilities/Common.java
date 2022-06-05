package com.ralvarez20.shopit_client.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ralvarez20.shopit_client.Login;

import io.paperdb.Paper;

public class Common {

    public static boolean verifySavedToken(SharedPreferences prefs){
        return prefs.contains("token") && prefs.getString("token", "") != null && !prefs.getString("token", "").isEmpty();
    }

    public static String getTokenValue(SharedPreferences prefs){
        return prefs.getString("token","");
    }

    public static void removeToken(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("token");
        editor.commit();
    }

}
