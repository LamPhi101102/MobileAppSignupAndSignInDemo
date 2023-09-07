package com.example.myapplication.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.datatransport.runtime.dagger.multibindings.StringKey;

// this function is used to easy to manage and query data
public class PreferenceManager {
    // allow acess and edit data
    private final SharedPreferences sharedPreferences;

    // it is constructor that is used to receive KEY_PREFERENCE_NAME and then access with MODE_PRIVATE
    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    // it is used to store Boolean value in sharedPreferences under key-value
    // besides, can edit data
    // apply is used to store changes
    public void putBoolean(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    // it is used to get Boolean from sharedPreferences based on String key
    // if there is no values it always return false
    public Boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    // it is used to store String value in sharedPreferences under key-value
    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    // it is used to get String from sharedPreferences based on String key
    // if there is no values it always return false
    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }
    // Delete Function
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
