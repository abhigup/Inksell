package services;

import models.VerifyUserEntity;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Abhinav on 08/04/15.
 */
public interface IPostServices {


    @POST("/registeruser")
    void registerUser(@Body VerifyUserEntity userEntity, Callback<String> callback);
}
