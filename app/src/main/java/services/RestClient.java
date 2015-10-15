package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import Constants.InksellConstants;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Abhinav on 05/04/15.
 */
public class RestClient {
    private static IGetServices GET_REST_CLIENT;
    private static IPostServices POST_REST_CLIENT;

    private static String ROOT =
            "http://inksell.cloudapp.net:8080/service1.svc/json/";

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

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);

        Gson gson = new GsonBuilder()
                .setDateFormat(InksellConstants.DATEFORMAT)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


//        Gson gson = new GsonBuilder()
//                .setDateFormat(InksellConstants.DATEFORMAT)
//                .create();
//        GsonConverter gsonConverter = new GsonConverter(gson);
//        RestAdapter.Builder getbuilder = new RestAdapter.Builder()
//                .setEndpoint(ROOT)
//                .setConverter(gsonConverter)
//                .setClient(new OkClient(okHttpClient))
//                .setErrorHandler(new CustomErrorHandler(ConfigurationManager.CurrentActivityContext));
//
//        RestAdapter getRestAdapter = getbuilder.build();
        GET_REST_CLIENT = retrofit.create(IGetServices.class);

//        RestAdapter.Builder postbuilder = new RestAdapter.Builder()
//                .setEndpoint(ROOT)
//                .setConverter(gsonConverter)
//                .setClient(new OkClient(okHttpClient))
//                .setErrorHandler(new CustomErrorHandler(ConfigurationManager.CurrentActivityContext));

        //RestAdapter postRestAdapter = postbuilder.build();
        POST_REST_CLIENT = retrofit.create(IPostServices.class);
    }
}
