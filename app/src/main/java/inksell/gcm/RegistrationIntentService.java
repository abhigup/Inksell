package inksell.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Constants.AppData;
import Constants.StorageConstants;
import inksell.inksell.R;
import models.SubscriptionEntity;
import services.InksellCallback;
import services.RestClient;
import utilities.LocalStorageHandler;
import utilities.ResponseStatus;

/**
 * Created by Abhinav on 21/10/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            String localToken = LocalStorageHandler.GetData(StorageConstants.UserNotificationToken, String.class);

            AppData.NotificationToken = token;

            if(localToken==null || localToken.compareTo(token)!=0) {
                LocalStorageHandler.SaveData(StorageConstants.UserNotificationToken, token);
                updateTokenForSubscriptions();
            }

        } catch (Exception e) {;
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            //sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
    }

    private void updateTokenForSubscriptions() {

        List<SubscriptionEntity> subscriptionEntityList = getUpdatedLocalSubscriptions();
        if(subscriptionEntityList!=null && !subscriptionEntityList.isEmpty()) {
            RestClient.post().updateAllSubscriptionUserUriV2(subscriptionEntityList).enqueue(new InksellCallback<Integer>() {
                @Override
                public void onSuccess(Integer integer) {

                }
                @Override
                public void onError(ResponseStatus responseStatus)
                {

                }
            });
        }
    }

    private List<SubscriptionEntity>  getUpdatedLocalSubscriptions()
    {
        List<SubscriptionEntity> subscriptionEntityList = new ArrayList<>();
        SubscriptionEntity[] subscriptionEntities = LocalStorageHandler.GetData(StorageConstants.SubscriptionTagEntities, SubscriptionEntity[].class);
        if(subscriptionEntities!=null && subscriptionEntities.length>0) {
            subscriptionEntityList = new ArrayList(Arrays.asList(subscriptionEntities));
            for (int i = 0; i < subscriptionEntityList.size(); i++) {
                subscriptionEntityList.get(i).userUri = AppData.NotificationToken;
            }
        }
        return subscriptionEntityList;
    }

}
