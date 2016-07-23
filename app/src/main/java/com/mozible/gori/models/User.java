package com.mozible.gori.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JunLee on 7/18/16.
 */
public class User {
    public int id;
    public String username;

    public static String makeJSon(List<User> userList) {
        String gsonResult = null;
        Gson gson = new Gson();

        gsonResult = gson.toJson(userList);

        return gsonResult;
    }

    public static ArrayList<User> getListFromJSonObject(String jsonObject) {
        ArrayList<User> result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<ArrayList<User>>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}
