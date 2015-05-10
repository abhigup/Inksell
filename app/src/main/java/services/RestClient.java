package services;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import utilities.ConfigurationManager;

/**
 * Created by Abhinav on 05/04/15.
 */
public class RestClient {
    private static IGetServices GET_REST_CLIENT;
    private static IPostServices POST_REST_CLIENT;

    private static String ROOT =
            "http://inksell.cloudapp.net:8080/service1.svc/json";

    static {
        setupRestClient();
    }

    private RestClient() {}

    public static IGetServices get() {
        return GET_REST_CLIENT;
    }

    public static IPostServices post() {
        return POST_REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder getbuilder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setErrorHandler(new CustomErrorHandler(ConfigurationManager.CurrentActivityContext));

        RestAdapter getRestAdapter = getbuilder.build();
        GET_REST_CLIENT = getRestAdapter.create(IGetServices.class);

        RestAdapter.Builder postbuilder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()))
                .setErrorHandler(new CustomErrorHandler(ConfigurationManager.CurrentActivityContext));

        RestAdapter postRestAdapter = postbuilder.build();
        POST_REST_CLIENT = postRestAdapter.create(IPostServices.class);
    }
}
