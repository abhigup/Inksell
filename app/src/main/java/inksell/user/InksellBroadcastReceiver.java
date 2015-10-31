package inksell.user;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import Constants.InksellConstants;
import models.PostSummaryEntity;
import utilities.ConfigurationManager;
import utilities.FavouritesHelper;
import utilities.Utility;

/**
 * Created by Abhinav on 31/10/15.
 */
public class InksellBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        ConfigurationManager.CurrentActivityContext = context;

        if(InksellConstants.NotificationAddToFavourites.equals(action)) {
            String stringExtra = intent.getStringExtra("postSummary");
            if(stringExtra!=null)
            {
                PostSummaryEntity postSummaryEntity = Utility.GetObjectFromJSON(stringExtra, PostSummaryEntity.class);
                FavouritesHelper.setFavouritesFromLocal();
                FavouritesHelper.AddToFavourites(postSummaryEntity);

                Log.d("PostId", ""+postSummaryEntity.PostId);

                NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(postSummaryEntity.PostId);
            }
        }
    }
}
