package services;

import java.util.List;

import models.SearchEntity;
import models.VerifyUserEntity;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Abhinav on 08/04/15.
 */
public interface IPostServices {


    @POST("/registeruser")
    void registerUser(@Body VerifyUserEntity userEntity, Callback<String> callback);

    @POST("/addCompanyRequest/{company}/{location}/{name}")
    void newCompanyRequest(@Path("company") String company, @Path("location") String location, @Path("name") String name, @Body String email, Callback<String> callback);

    @POST("/SearchTextV2/{companyid}/{locationid}/{categoryid}/{userguid}")
    void searchTextV2(@Path("companyid") int companyId, @Path("locationid") int locationId, @Path("categoryid") int categoryId, @Path("userguid") String userGuid, @Body String queryText, Callback<List<SearchEntity>> callback);


}
