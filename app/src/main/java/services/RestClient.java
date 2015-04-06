package services;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Abhinav on 05/04/15.
 */
public class RestClient {
    private static IServices REST_CLIENT;
    private static String ROOT =
            "http://inksell.cloudapp.net:8080/service1.svc/json";

    static {
        setupRestClient();
    }

    private RestClient() {}

    public static IServices get() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient()));

        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(IServices.class);
    }
}
