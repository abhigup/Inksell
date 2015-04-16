package services;

import java.util.List;

import models.CompanyEntity;
import models.LocationEntity;
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

    @GET("/verifyUser/{guid}/{code}/0")
    void verifyNewUser(@Path("guid") String guid, @Path("code") String code, Callback<Integer> callback);

    @GET("/registerUserAgain/{email}")
    void registerUserAgain(@Path("email") String email, Callback<String> callback);
}
