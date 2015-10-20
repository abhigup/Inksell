package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import Constants.InksellConstants;
import inksell.inksell.R;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import utilities.ConfigurationManager;

/**
 * Created by Abhinav on 05/04/15.
 */
public class RestClient {
    private static IGetServices GET_REST_CLIENT;
    private static IPostServices POST_REST_CLIENT;

    private static String HTTPSROOT =
            "https://inksellsecure.cloudapp.net/Service1.svc/json/";

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

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = ConfigurationManager.CurrentActivityContext.getResources().openRawResource(R.raw.inksellcert);
            Certificate ca;
            ca = cf.generateCertificate(cert);
            cert.close();

            String keyStroreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStroreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgo = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgo);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            okHttpClient.setSslSocketFactory(sslContext.getSocketFactory());

        }
        catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);

        Gson gson = new GsonBuilder()
                .setDateFormat(InksellConstants.DATEFORMAT)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(HTTPSROOT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        GET_REST_CLIENT = retrofit.create(IGetServices.class);

        POST_REST_CLIENT = retrofit.create(IPostServices.class);
    }
}
