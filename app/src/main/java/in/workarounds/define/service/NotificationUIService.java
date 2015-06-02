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

    @Override
    protected void handleIntent(Intent intent) {
        boolean fromNotification = intent
                .getBooleanExtra(INTENT_EXTRA_FROM_NOTIFICATION, false);
        if(fromNotification){
            removeNotification();
            goToState(STATE_CARD);
        } else {
            initCard(getCardView());
            String clipText = getClipTextFromIntent(intent);
            if(clipText == null){
                LogUtils.LOGD(TAG, "No clip text in the intent");
                stopSelf();
                return;
            }
            showNotification(clipText);
            handleClipText(clipText);
        }
    }

    private void showNotification(String clipText){
        String title = "Select a word";
        Intent intent = new Intent(this, NotificationUIService.class);
        intent.putExtra(INTENT_EXTRA_FROM_NOTIFICATION, true);
        intent.putExtra(INTENT_EXTRA_CLIPTEXT, clipText);

        PendingIntent pendingIntent = PendingIntent
                .getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(clipText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[0])
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    private void removeNotification(){
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(1);
    }
}
