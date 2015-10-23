package services;

import java.util.List;

import models.AutomobileEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.MultiplePostDetailsEntity;
import models.OtherEntity;
import models.RealEstateEntity;
import models.SearchEntity;
import models.SubscriptionEntity;
import models.UserEntity;
import models.VerifyUserEntity;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Abhinav on 08/04/15.
 */
public interface IPostServices {


    @POST("registeruser")
    Call<String> registerUser(@Body VerifyUserEntity userEntity);

    @POST("addCompanyRequest/{company}/{location}/{name}")
    Call<String> newCompanyRequest(@Path("company") String company, @Path("location") String location, @Path("name") String name, @Body String email);

    @POST("SearchTextV2/{companyid}/{locationid}/{categoryid}/{userguid}")
    Call<List<SearchEntity>> searchTextV2(@Path("companyid") int companyId, @Path("locationid") int locationId, @Path("categoryid") int categoryId, @Path("userguid") String userGuid, @Body String queryText);

    @POST("createUsers")
    Call<Integer> CreateUpdateUserData(@Body UserEntity userEntity);

    @POST("addOtherPost/{isMultiple}")
    Call<Integer> addOtherPost(@Body OtherEntity iPostEntity, @Path("isMultiple") Integer isMultiple);

    @POST("addAutomobilePost/{isMultiple}")
    Call<Integer> addAutomobilePost(@Body AutomobileEntity iPostEntity, @Path("isMultiple") Integer isMultiple);

    @POST("addFurniturePost/{isMultiple}")
    Call<Integer> addFurniturePost(@Body FurnitureEntity iPostEntity, @Path("isMultiple") Integer isMultiple);

    @POST("addRealstatePost/{isMultiple}")
    Call<Integer> addRealEstatePost(@Body RealEstateEntity iPostEntity, @Path("isMultiple") Integer isMultiple);

    @POST("addElectronicsPost/{isMultiple}")
    Call<Integer> addElectronicsPost(@Body ElectronicEntity iPostEntity, @Path("isMultiple") Integer isMultiple);

    @POST("addMultipleItem")
    Call<Integer> addMultiplePost(@Body MultiplePostDetailsEntity multiplePostDetailsEntity);


    @POST("AddSubscriptionV2")
    Call<String> addSubscriptionV2(@Body SubscriptionEntity subscriptionEntity);

    @POST("RemoveListedSubscriptionV2")
    Call<Integer> removeListedSubscriptionV2(@Body List<SubscriptionEntity> subscriptionEntities);

    @POST("UpdateAllSubscriptionUserUriV2")
    Call<Integer> updateAllSubscriptionUserUriV2(@Body List<SubscriptionEntity> subscriptionEntities);
}
