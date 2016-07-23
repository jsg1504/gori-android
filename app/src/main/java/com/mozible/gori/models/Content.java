package com.mozible.gori.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JunLee on 7/18/16.
 */
public class Content extends MainAdapterObject{
    public int id;
    public UserProfile user_profile;
    public String image_file;
    public String description;
    public String created_at;
    public int content_type;


    public static String makeJSon(List<Content> contentList) {
        String gsonResult = null;
        Gson gson = new Gson();

        gsonResult = gson.toJson(contentList);

        return gsonResult;
    }

    public static ArrayList<Content> getListFromJSonObject(String jsonObject) {
        ArrayList<Content> result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<ArrayList<Content>>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }

}
