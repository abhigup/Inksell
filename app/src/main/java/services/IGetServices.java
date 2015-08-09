package services;

import java.util.List;

import models.AutomobileEntity;
import models.CompanyEntity;
import models.ElectronicEntity;
import models.FurnitureEntity;
import models.LocationEntity;
import models.OtherEntity;
import models.PostSummaryEntity;
import models.RealEstateEntity;
import models.UserEntity;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Abhinav on 05/04/15.
 */
public interface IGetServices {

    @GET("/getCompanies")
    void getCompanies(Callback<List<CompanyEntity>> callback);

    @GET("/getLocations/{companyId}")
    void getLocations(@Path("companyId") int companyId, Callback<List<LocationEntity>> callback);

    @GET("/verifyUser/{guid}/{code}/{isAlreadyRegistered}")
    void verifyNewUser(@Path("guid") String guid, @Path("code") String code, @Path("isAlreadyRegistered") int isAlreadyRegistered, Callback<Integer> callback);

    @GET("/registerUserAgain/{email}")
    void registerUserAgain(@Path("email") String email, Callback<String> callback);

    @GET("/GetUserEntityDevFriendly/{guid}")
    void getUserDetails(@Path("guid") String guid, Callback<UserEntity> userEntity);

    @GET("/GetPostSummaryAll/{lastPostId}/{guid}")
    void getPostSummaryAll(@Path("lastPostId") int lastPostId, @Path("guid") String guid, Callback<List<PostSummaryEntity>> postSummaryList);

    @GET("/GetFilteredPostSummary/{lastPostId}/{categoryId}/{guid}")
    void getFilteredPostSummary(@Path("lastPostId") int lastPostId, @Path("categoryId") int categoryId, @Path("guid") String guid, Callback<List<PostSummaryEntity>> postSummaryList);

    @GET("/GetFullAutomobilePost/{postId}/{userGuid}")
    void getAutomobilesFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid, Callback<AutomobileEntity> entityCallback);

    @GET("/GetFullElectronicPost/{postId}/{userGuid}")
    void getElectronicsFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid, Callback<ElectronicEntity> entityCallback);

    @GET("/GetFullOtherPost/{postId}/{userGuid}")
    void getOtherFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid, Callback<OtherEntity> entityCallback);

    @GET("/GetFullFurniturePost/{postId}/{userGuid}")
    void getFurnitureFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid, Callback<FurnitureEntity> entityCallback);

    @GET("/GetFullRealStatePost/{postId}/{userGuid}")
    void getRealEstateFullPostEntity(@Path("postId") int postId, @Path("userGuid") String guid, Callback<RealEstateEntity> entityCallback);
}
