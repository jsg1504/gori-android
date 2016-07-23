package com.mozible.gori.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JunLee on 7/18/16.
 */
public class UserProfile extends MainAdapterObject{
    public User user;
    public String nick_name;
    public String profile_image;
    public String description;
    public String last_login;
    public float latitude;
    public float longitude;
    public String job;

    public boolean isFollow = false;
    public int user_content_count = -1;
    public int user_follower_count = -1;
    public int user_following_count = -1;

    public String getJobInAdapter() {
        if(job == null) {
            return "";
        } else {
            return "[" + job + "]";
        }
    }

    public String getDescription() {
        if(description == null) {
            return "";
        } else {
            return description;
        }
    }

    public String getJob() {
        if(job == null) {
            return "";
        } else {
            return job;
        }
    }
    public static String makeJSon(List<UserProfile> userList) {
        String gsonResult = null;
        Gson gson = new Gson();

        gsonResult = gson.toJson(userList);

        return gsonResult;
    }

    public static ArrayList<UserProfile> getListFromJSonObjects(String jsonObject) {
        ArrayList<UserProfile> result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<ArrayList<UserProfile>>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }

    public static String makeJSon(UserProfile user) {
        String gsonResult = null;
        Gson gson = new Gson();

        gsonResult = gson.toJson(user);

        return gsonResult;
    }

    public static UserProfile getObjectFromJSonObject(String jsonObject) {
        UserProfile result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<UserProfile>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}
