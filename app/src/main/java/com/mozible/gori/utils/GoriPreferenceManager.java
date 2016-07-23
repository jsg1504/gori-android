package com.mozible.gori.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mozible.gori.models.UserProfile;

/**
 * Created by JunLee on 7/18/16.
 */
public class GoriPreferenceManager {
    private static GoriPreferenceManager goriPreferenceManager = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private static final String PREF_GORI = "PREF_GORI";
    private static final String PREF_SESSION = "pref_serssion";
    private static final String PREF_USERNAME = "pref_username";
    private static final String PREF_PASSWORD = "pref_password";
    private static final String PREF_MY_PROFILE = "pref_my_profile";

    private GoriPreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_GORI, context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static GoriPreferenceManager getInstance(Context context) {
        if ( goriPreferenceManager == null ) goriPreferenceManager = new GoriPreferenceManager(context);
        return goriPreferenceManager;
    }

    public UserProfile getMyProfileObject() {
        String json = getMyProfile();
        if(!GoriTextUtil.isEmpty(json)) {
            return UserProfile.getObjectFromJSonObject(json);
        } else {
            return null;
        }
    }

    public void setMyProfileObject(UserProfile userProfile) {
        String json = UserProfile.makeJSon(userProfile);
        setMyProfile(json);
    }

    public String getMyProfile() {
        return prefs.getString(PREF_MY_PROFILE, "");
    }
    public void setMyProfile(String userProfileJson) {
        editor.putString(PREF_MY_PROFILE, userProfileJson);
        editor.commit();
    }

    public String getSession() {
        return prefs.getString(PREF_SESSION, "");
    }

    public void setSession(String session) {
        editor.putString(PREF_SESSION, session);
        editor.commit();
    }
    public String getUsername() {
        return prefs.getString(PREF_USERNAME, "");
    }

    public void setUsername(String session) {
        editor.putString(PREF_USERNAME, session);
        editor.commit();
    }
    public String getPassword() {
        return prefs.getString(PREF_PASSWORD, "");
    }

    public void setPassword(String session) {
        editor.putString(PREF_PASSWORD, session);
        editor.commit();
    }

}
