package inksell.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import Constants.InksellConstants;
import inksell.inksell.R;
import inksell.inksell.StartPage;
import models.PostSummaryEntity;
import utilities.Utility;

/**
 * Created by Abhinav on 21/10/15.
 */
public class InksellGcmListenerService extends GcmListenerService {

    private static final String TAG = "InksellGcmListenerService";
    private NotificationManager _notificationManeger;

    private PostSummaryEntity postSummaryEntity = new PostSummaryEntity();

    @Override
    public void onMessageReceived(String from, Bundle data) {

        postSummaryEntity.Title = data.getString("PostTitle");
        postSummaryEntity.Postdate = Utility.StringDateToDate(data.getString("PostedOn"));
        postSummaryEntity.PostedBy = data.getString("PostedBy");
        postSummaryEntity.PostId = Integer.parseInt(data.getString("PostId"));
        postSummaryEntity.categoryid = Integer.parseInt(data.getString("CategoryId"));

        Log.d("NotificationId", "NoteId: "+postSummaryEntity.PostId);

        createNotification();


    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, StartPage.class);
        notificationIntent.putExtra("postSummary", Utility.GetJSONString(postSummaryEntity));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, postSummaryEntity.PostId, notificationIntent, 0);

        _notificationManeger = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentTitle(postSummaryEntity.Title)
                        .setContentText(postSummaryEntity.PostedBy)
                        .setColor(Color.argb(255, 1, 87, 155))
                        .setSmallIcon(R.drawable.ic_app_icon)
                        .setContentIntent(pendingIntent);

        Intent addToFav = new Intent();
        addToFav.setAction(InksellConstants.NotificationAddToFavourites);
        addToFav.putExtra("postSummary", Utility.GetJSONString(postSummaryEntity));
        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, postSummaryEntity.PostId, addToFav, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.notify_fav, getString(R.string.addToFavourites), pendingIntentYes);

        _notificationManeger.notify(postSummaryEntity.PostId, builder.build());
    }
}

