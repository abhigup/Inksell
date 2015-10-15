package services;

import java.util.List;

import models.AutomobileEntity;
import models.CompanyEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.LocationEntity;
import models.OtherEntity;
import models.PostSummaryEntity;
import models.PropertyMapEntity;
import models.RealEstateEntity;
import models.TagsEntity;
import models.UserEntity;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Abhinav on 05/04/15.
 */
public interface IGetServices {

    @GET("getCompanies")
    Call<List<CompanyEntity>> getCompanies();

    @GET("getLocations/{companyId}")
    Call<List<LocationEntity>> getLocations(@Path("companyId") int companyId);

    @GET("verifyUser/{guid}/{code}/{isAlreadyRegistered}")
    Call<Integer> verifyNewUser(@Path("guid") String guid, @Path("code") String code, @Path("isAlreadyRegistered") int isAlreadyRegistered);

    @GET("registerUserAgain/{email}")
    Call<String> registerUserAgain(@Path("email") String email);

    @GET("GetUserEntityDevFriendly/{guid}")
    Call<UserEntity> getUserDetails(@Path("guid") String guid);

    @GET("GetPostSummaryAll/{lastPostId}/{guid}")
    Call<List<PostSummaryEntity>> getPostSummaryAll(@Path("lastPostId") int lastPostId, @Path("guid") String guid);

    @GET("GetMyPostSummary/{userGuid}")
    Call<List<PostSummaryEntity>> getMyPostSummary(@Path("userGuid") String UserGuid);

    @GET("GetFilteredPostSummary/{lastPostId}/{categoryId}/{guid}")
    Call<List<PostSummaryEntity>> getFilteredPostSummary(@Path("lastPostId") int lastPostId, @Path("categoryId") int categoryId, @Path("guid") String guid);

    @GET("GetFullAutomobilePost/{postId}/{userGuid}")
    Call<AutomobileEntity> getAutomobilesFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid);

    @GET("GetFullElectronicPost/{postId}/{userGuid}")
    Call<ElectronicEntity> getElectronicsFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid);

    @GET("GetFullOtherPost/{postId}/{userGuid}")
    Call<OtherEntity> getOtherFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid);

    @GET("GetFullFurniturePost/{postId}/{userGuid}")
    Call<FurnitureEntity> getFurnitureFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid);

    @GET("GetFullRealStatePost/{postId}/{userGuid}")
    Call<RealEstateEntity> getRealEstateFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid);

    @GET("GetMultiplePost/{postId}/{userGuid}")
    Call<List<PostSummaryEntity>> getMultipleFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid);

    @GET("DeletePost/{postId}/{userGuid}/{category}")
    Call<Integer> deletePost(@Path("postId") int postId, @Path("userGuid") String guid, @Path("category") int category);

    @GET("getAllTags")
    Call<List<TagsEntity>> getAllSubscriptionsTags();

    @GET("GetPropertyMapSummary/{distance}/{userguid}/{latitude}/{longitude}")
    Call<List<PropertyMapEntity>> getMapSummary(@Path("distance") String distance, @Path("userguid") String guid, @Path("latitude") String latitude,  @Path("longitude") String longitude);

}
