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
 * The service that should be triggered when user clips a text
 * and has a preference set to show notification(Heads up/ Silent)
 */
public class NotificationUIService extends DefineUIService{
    private static String TAG = LogUtils.makeLogTag(NotificationUIService.class);
    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_CLICKED_REQUEST_ID = 11;
    public static final int NOTIFICATION_DISMISSED_REQUEST_ID = 10;
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
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void removeNotification(){
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(NOTIFICATION_ID);
    }

    /**
     * Sets the correct title and content based the clipped text
     * @param builder Notification Builder object
     * @param clipText Clipped Text by the user
     * @return Notification Builder object with content set
     */
    private NotificationCompat.Builder buildContent(NotificationCompat.Builder builder, String clipText){
        String title = "Select a word";

        return builder
                .setContentTitle(title)
                .setContentText(clipText)
                .setSmallIcon(R.mipmap.ic_launcher);
    }

    /**
     * Sets the click and delete intents for the notification
     * @param builder Notification Builder object
     * @return Notification Builder object with Intents set
     */
    private NotificationCompat.Builder buildIntents(NotificationCompat.Builder builder){
        PendingIntent pendingClickIntent = getClickIntent();
        PendingIntent pendingDismissIntent = getDismissIntent();

        return builder
                .setContentIntent(pendingClickIntent)
                .setDeleteIntent(pendingDismissIntent);
    }

    /**
     * Returns the PendingIntent to be triggered when notification
     * is clicked
     * @return The PendingIntent to be triggered when notification
     * is clicked
     */
    private PendingIntent getClickIntent(){
        Intent clickIntent = new Intent(this, NotificationUIService.class);
        clickIntent.putExtra(INTENT_EXTRA_NOTIFICATION_CLICKED, true);

        return PendingIntent
                .getService(this, NOTIFICATION_CLICKED_REQUEST_ID, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Returns the PendingIntent to be triggered when notification
     * is dismissed
     * @return The PendingIntent to be triggered when notification
     * is dismissed
     */
    private PendingIntent getDismissIntent(){
        Intent dismissIntent = new Intent(this, NotificationUIService.class);
        dismissIntent.putExtra(INTENT_EXTRA_NOTIFICATION_DISMISSED, true);

        return PendingIntent
                .getService(this, NOTIFICATION_DISMISSED_REQUEST_ID, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Sets the correct priority to the notification based on user preferences
     * @param builder Notification Builder object
     * @return Notification Builder object with priority set
     */
    private NotificationCompat.Builder buildPriority(NotificationCompat.Builder builder){
        //TODO: get user preferences and set priority accordingly

        return builder
                .setVibrate(new long[0])
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }
}
