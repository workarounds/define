package in.workarounds.define.service;

import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import in.workarounds.define.handler.LifeHandler;
import in.workarounds.define.util.LogUtils;

/**
 * Created by manidesto on 24/05/15.
 */
public class ChatHeadService extends DefineUIService{
    private static String TAG = LogUtils.makeLogTag(ChatHeadService.class);

    @Override
    protected void handleIntent(Intent intent) {
        initCard(getCardView());
        String clipText = getClipTextFromIntent(intent);
        if(clipText == null){
            LogUtils.LOGD(TAG, "No clip text in the intent");
            stopSelf();
            return;
        }
        handleClipText(clipText);
        goToState(STATE_BUBBLE);
        //TODO: get die out time from shared preferences
        int dieOutTime = 10000;
        mHandler.sendEmptyMessageDelayed(LifeHandler.MSG_DIE_OUT, dieOutTime);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getBubbleView().animate()
                    .alpha(0.0f)
                    .setDuration(dieOutTime)
                    .setInterpolator(new AccelerateInterpolator(5.0f))
                    .start();
        }
    }

    @Override
    protected void onBubbleClicked(View bubble) {
        mHandler.sendEmptyMessage(LifeHandler.MSG_STAY_ALIVE);
        super.onBubbleClicked(bubble);
    }

    @Override
    public void onStayAlive() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getBubbleView().animate().cancel();
            getBubbleView().setAlpha(1.0f);
        }
    }
}
