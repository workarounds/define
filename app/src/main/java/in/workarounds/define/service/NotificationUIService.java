package in.workarounds.define.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import in.workarounds.define.R;
import in.workarounds.define.util.LogUtils;

/**
 * Created by manidesto on 02/06/15.
 */
public class NotificationUIService extends DefineUIService{
    private static String TAG = LogUtils.makeLogTag(NotificationUIService.class);
    public static final String INTENT_EXTRA_NOTIFICATION_CLICKED = "intent_extra_notification_clicked";
    public static final String INTENT_EXTRA_NOTIFICATION_DISMISSED = "intent_extra_notification_dismissed";

    @Override
    protected void handleIntent(Intent intent) {
        boolean notificationClicked = intent
                .getBooleanExtra(INTENT_EXTRA_NOTIFICATION_CLICKED, false);
        boolean notificationDismissed = intent
                .getBooleanExtra(INTENT_EXTRA_NOTIFICATION_DISMISSED, false);

        if(notificationClicked){
            removeNotification();
            goToState(STATE_CARD);
        } else if(notificationDismissed) {
            goToState(STATE_WAITING);
            stopSelf();
        } else{
            String clipText = getClipTextFromIntent(intent);
            if(clipText == null){
                LogUtils.LOGD(TAG, "No clip text in the intent");
                stopSelf();
                return;
            }
            initCard(getCardView());
            showNotification(clipText);
            handleClipText(clipText);
        }
    }

    private void showNotification(String clipText){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder = buildContent(builder, clipText);
        builder = buildIntents(builder);
        builder = buildPriority(builder);

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void removeNotification(){
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(1);
    }

    private NotificationCompat.Builder buildContent(NotificationCompat.Builder builder, String clipText){
        String title = "Select a word";

        return builder
                .setContentTitle(title)
                .setContentText(clipText)
                .setSmallIcon(R.mipmap.ic_launcher);
    }

    private NotificationCompat.Builder buildIntents(NotificationCompat.Builder builder){
        PendingIntent pendingClickIntent = getClickIntent();
        PendingIntent pendingDismissIntent = getDismissIntent();

        return builder
                .setContentIntent(pendingClickIntent)
                .setDeleteIntent(pendingDismissIntent);
    }

    private PendingIntent getClickIntent(){
        Intent clickIntent = new Intent(this, NotificationUIService.class);
        clickIntent.putExtra(INTENT_EXTRA_NOTIFICATION_CLICKED, true);

        return PendingIntent
                .getService(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getDismissIntent(){
        Intent dismissIntent = new Intent(this, NotificationUIService.class);
        dismissIntent.putExtra(INTENT_EXTRA_NOTIFICATION_DISMISSED, true);

        return PendingIntent
                .getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Builder buildPriority(NotificationCompat.Builder builder){
        //TODO: get user preferences and set priority accordingly

        return builder
                .setVibrate(new long[0])
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }
}
