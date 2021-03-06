package com.project.emi.eventscape.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class MySharedPref {
    private Context ctx;

    private SharedPreferences.Editor editor;

    public MySharedPref(Context ctx){
        this.ctx=ctx;
    }


    public void saveInSharedPref(String key, int value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void saveStringInSharedPref(String key, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public  void saveObjectToSharedPreference(String serializedObjectKey, Object object) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(object);
        sharedPreferencesEditor.putString(serializedObjectKey, serializedObject);
        sharedPreferencesEditor.apply();
    }

    public  <GenericClass> GenericClass getSavedObjectFromPreference(String preferenceKey, Class<GenericClass> classType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new GsonBuilder().setLenient().create();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
    }

}
