package com.mozible.gori.utils;

import android.graphics.Bitmap;
import android.telecom.Call;

import com.mozible.gori.models.Content;
import com.mozible.gori.models.PostResult;
import com.mozible.gori.models.UserProfile;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by JunLee on 7/22/16.
 */
public interface ServerInterface {

    @Multipart
    @POST("/user/update/profile_image/")
    void uploadProfileImage(@Header("Cookie") String session, @Part("profile_image")TypedFile imageFile, Callback<PostResult> callback);

    @GET("/user/detail/{username}/")
    void getUserInfoByUserName(@Header("Cookie") String session, @Path("username") String username, Callback<UserProfile> callback);

    @GET("/content/get/by/user/{username}/")
    void getUserContentsByUserName(@Header("Cookie") String session, @Path("username") String username, Callback<ArrayList<Content>> callback);

    @GET("/content/get/all/last/")
    void getAllContents(@Header("Cookie") String session, Callback<ArrayList<Content>> callback);

    @Multipart
    @POST("/content/upload1/")
    void uploadContent1(@Header("Cookie") String session, @Part("content_type") int content_type, Callback<PostResult> callback);

    @Multipart
    @POST("/content/upload2/{content_id}/")
    void uploadContent2(@Header("Cookie") String session, @Part("image_file") TypedFile imageFile, @Path("content_id") int contentId, Callback<PostResult> callback);

    @Multipart
    @POST("/content/upload3/{content_id}/")
    void uploadContent3(@Header("Cookie") String session, @Part("description") String description, @Path("content_id") int contentId, Callback<PostResult> callback);

    @POST("/content/upload/cancel/{content_id}/")
    void cancelUploadContent(@Header("Cookie") String session, @Path("content_id") int contentId, @Body String emptyString, Callback<PostResult> callback);

    @GET("/user/get/following/{username}/")
    void getUserFollowings(@Header("Cookie") String session, @Path("username") String username, Callback<ArrayList<UserProfile>> callback);

    @GET("/user/get/follower/{username}/")
    void getUserFollowers(@Header("Cookie") String session, @Path("username") String username, Callback<ArrayList<UserProfile>> callback);

    @POST("/user/follow/{username}/")
    void followUser(@Header("Cookie") String session, @Path("username") String username, @Body String emptyString, Callback<PostResult> callback);

    @POST("/user/un_follow/{username}/")
    void unFollowUser(@Header("Cookie") String session, @Path("username") String username, @Body String emptyString, Callback<PostResult> callback);

    @POST("/accounts/logout/")
    void logout(@Header("Cookie") String session, @Body String emptyString, Callback<PostResult> callback);

    @Multipart
    @POST("/accounts/update/")
    void updateMyInfo(@Header("Cookie") String session, @Part("is_expert") int isExpert,
                      @Part("nick_name") String nickName, @Part("description") String description,
                      @Part("latitude") float latitude, @Part("longitude") float longitude,
                      @Part("location") String location, @Part("job") String job,
                      Callback<PostResult> callback);
}
