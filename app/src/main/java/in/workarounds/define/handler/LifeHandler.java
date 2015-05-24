package in.workarounds.define.handler;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import in.workarounds.define.service.DefineUIService;
import in.workarounds.define.util.LogUtils;

/**
 * Created by manidesto on 24/05/15.
 */
public class LifeHandler extends Handler{
    private static String TAG = LogUtils.makeLogTag(LifeHandler.class);
    public static final int MSG_STAY_ALIVE = 0;
    public static final int MSG_DIE_OUT = 1;

    WeakReference<DefineUIService> mServiceRef;

    public LifeHandler(DefineUIService service){
        mServiceRef = new WeakReference<>(service);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case MSG_DIE_OUT:
                dieOut();
                LogUtils.LOGD(TAG, "Dieing out");
                break;
            case MSG_STAY_ALIVE:
                LogUtils.LOGD(TAG, "Staying alive");
                stayAlive();
                break;
            default :
                LogUtils.LOGD(TAG, "Unknown message type :" + msg);
                break;
        }
    }

    private void dieOut(){
        DefineUIService service = mServiceRef.get();
        if(service != null){
            service.onDieOut();
            service.onCloseDialogs();
        }
        this.removeMessages(MSG_DIE_OUT);
        this.removeMessages(MSG_STAY_ALIVE);
    }

    private void stayAlive(){
        DefineUIService service = mServiceRef.get();
        if(service != null){
            service.onStayAlive();
            this.removeMessages(MSG_DIE_OUT);
        } else {
            this.removeMessages(MSG_DIE_OUT);
            this.removeMessages(MSG_STAY_ALIVE);
        }
    }
}
