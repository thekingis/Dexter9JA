package com.dexter9ja.android.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SharedPrefMngr {

    private final Context mCtx;
    private static final String shrdPrefName = "myShrdPrefName";
    private static final String userId = "userId";
    private static final String userUsername = "userUsername";
    private static final String userFirstName = "userFirstName";
    private static final String userLastName = "userLastName";
    private static final String userEmail = "userEmail";
    private static final String userType = "userType";
    private static final String userIsActive = "userIsActive";
    private static final String userIsSuperUser = "userIsSuperUser";
    private static final String userIsStaff = "userIsStaff";

    public SharedPrefMngr(Context mCtx){
        this.mCtx = mCtx;
    }

    public void storeUserInfo(JSONObject userObject) throws JSONException {
        SharedPreferences shrdPrf = mCtx.getSharedPreferences(shrdPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.putInt(userId, userObject.getInt("id"));
        editor.putString(userUsername, userObject.getString("username"));
        editor.putString(userFirstName, userObject.getString("first_name"));
        editor.putString(userLastName, userObject.getString("last_name"));
        editor.putString(userEmail, userObject.getString("email"));
        editor.putString(userType, userObject.getString("user_type"));
        editor.putBoolean(userIsActive, userObject.getBoolean("is_active"));
        editor.putBoolean(userIsSuperUser, userObject.getBoolean("is_superuser"));
        editor.putBoolean(userIsStaff, userObject.getBoolean("is_staff"));
        editor.apply();
    }

    public boolean loggedIn(){
        SharedPreferences shrdPrf = mCtx.getSharedPreferences(shrdPrefName, Context.MODE_PRIVATE);
        return shrdPrf.getInt(userId, 0) != 0;
    }

    public void logOut(){
        clearAll();
    }

    public void clearAll(){
        SharedPreferences shrdPrf = mCtx.getSharedPreferences(shrdPrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shrdPrf.edit();
        editor.remove(userId);
        editor.remove(userUsername);
        editor.remove(userFirstName);
        editor.remove(userLastName);
        editor.remove(userEmail);
        editor.remove(userType);
        editor.remove(userIsActive);
        editor.remove(userIsSuperUser);
        editor.remove(userIsStaff);
        editor.apply();
    }

    public int getUserID(){
        SharedPreferences shrdPrf = mCtx.getSharedPreferences(shrdPrefName, Context.MODE_PRIVATE);
        return shrdPrf.getInt(userId, 0);
    }

    public String getUserFirstName(){
        SharedPreferences shrdPrf = mCtx.getSharedPreferences(shrdPrefName, Context.MODE_PRIVATE);
        return shrdPrf.getString(userFirstName, null);
    }
}
