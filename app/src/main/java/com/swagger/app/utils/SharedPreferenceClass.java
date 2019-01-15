package com.swagger.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferenceClass {

    private static final String USER_PREFS = "userCred";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public SharedPreferenceClass(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(USER_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    //get value
    public int getValue_int(String intKeyValue) {

        return appSharedPrefs.getInt(intKeyValue, 10001);
    }

    public Long getValue_long(String longKeyValue) {
        return appSharedPrefs.getLong(longKeyValue, 0L);
    }

    public String getValue_string(String stringKeyValue) {
        return appSharedPrefs.getString(stringKeyValue, "");
    }

    public Boolean getValue_boolean(String stringKeyValue) {
        return appSharedPrefs.getBoolean(stringKeyValue, false);
    }

    public float getValue_float(String stringKeyValue) {
        return appSharedPrefs.getFloat(stringKeyValue,0);
    }

    public ArrayList<String> getValue_list(String stringKeyValue) {
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(stringKeyValue, "");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
    ///setvalue

    public void setValue_int(String intKeyValue, int _intValue) {

        prefsEditor.putInt(intKeyValue, _intValue).commit();
    }
    public void setValue_long(String longKeyValue, long _longValue) {

        prefsEditor.putLong(longKeyValue, _longValue).commit();
    }

    public void setValue_float(String longKeyValue, float _floatValue) {

        prefsEditor.putFloat(longKeyValue, _floatValue).commit();
    }

    public void setValue_string(String stringKeyValue, String _stringValue) {

        prefsEditor.putString(stringKeyValue, _stringValue).commit();

    }

    public void setValue_boolean(String stringKeyValue, Boolean _bool) {

        prefsEditor.putBoolean(stringKeyValue, _bool).commit();

    }

    public void setValue_list(String key_value, ArrayList<String> al) {
        Gson gson = new Gson();
        String json = gson.toJson(al);

        prefsEditor.putString(key_value, json).commit();

    }


    public void clearData() {
        prefsEditor.clear().commit();

    }
}
