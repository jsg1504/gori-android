package com.mozible.gori.models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by JunLee on 7/22/16.
 */
public class PostResult {
    public String status;
    public int content_id;
    public int error_code;
    public String desc;
    public UserProfile userProfile;
    public static PostResult getFromJSonObject(String jsonObject) {
        PostResult result = null;

        Gson gson = new Gson();
        Type resultType = new TypeToken<PostResult>() {
        }.getType();
        result = gson.fromJson(jsonObject, resultType);

        return result;
    }
}
