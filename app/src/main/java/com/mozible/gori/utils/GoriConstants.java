package com.mozible.gori.utils;

import com.squareup.okhttp.OkHttpClient;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by JunLee on 7/18/16.
 */
public class GoriConstants {
    public static String FOLLOWING_TABLE_NAME = "following_info_tbl";
    public static String FOLLOWER_TABLE_NAME = "follower_info_tbl";
    public static enum STATUS { SUCCESS, ERROR, ETC_ERROR}
    public static final String BASE_URL = "http://gori.mozible.com";

    public static String getLoginURL() {
        return BASE_URL + String.format("/accounts/login/");
    }
    public static String getUserInfoURL(String username) {
        return BASE_URL + String.format("/user/%s/", username);
    }
    public static String getSignUpURL() {
        return BASE_URL + String.format("/accounts/signup/");
    }

    public static String getMainInfoURL() {
        return BASE_URL + String.format("/content/get/by/following/");
    }

    public static String getMainInfoURL(String date) {
        return BASE_URL + String.format("/content/get/by/following/date/%s/", date);
    }

    public static String getContentListByUserURL(String username) {
        return BASE_URL + String.format("/content/get/by/user/%s/", username);
    }

    public static String getContentListByUserURL(String username, String date) {
        return BASE_URL + String.format("/content/get/by/user/%s/date/%s", username, date);
    }

    public static String getUserDetailURL(String username) {
        return BASE_URL + String.format("/user/detail2/%s/", username);
    }

    public static String getContentUpload1URL() {
        return BASE_URL + String.format("/content/upload1/");
    }

    public static String getContentUpload2URL(int contentId) {
        return BASE_URL + String.format("/content/upload2/%d/", contentId);
    }

    public static String getContentUpload3URL(int contentId) {
        return BASE_URL + String.format("/content/upload3/%d/", contentId);
    }

    public static String makeImageURL(String imagePath) {
        return BASE_URL + "/uploads/" + imagePath;
    }

    public static byte[] makePostDataBytes(HashMap<String, String> params) {
        byte[] result = null;
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append('&');
            }
            try {
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                result = postData.toString().getBytes("UTF-8");
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

}
