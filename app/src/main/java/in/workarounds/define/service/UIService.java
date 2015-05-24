package in.workarounds.define.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import in.workarounds.define.util.LogUtils;

/**
 * An abstract base class for a service that shows UI,
 * i.e draws over other apps like chat heads
 * The service has three states:
 *  - Waiting state
 *  - Bubble state
 *  - Card state
 *
 * Created by manidesto on 09/05/15.
 */
public abstract class UIService extends Service {
    private static String TAG = LogUtils.makeLogTag(UIService.class);
    /**
     * Service starts with this state.
     * In this state the Service doesn't show any UI on
     * the screen. It just waits for User action like
     * clicking on a notification, to switch to STATE_CARD. The
     * Service is expected to load the data or do background
     * jobs in this state(in the onCreate).
     */
    public static final int STATE_WAITING = 0;

    /**
     * In this state the Service shows a small bubble
     * and listens for touches in that small bubble, while
     * letting the User interact other apps. This state,
     * is similar to STATE_WAITING in many ways. We wait for
     * the User's action(tap on the bubble) to switch to
     * STATE_CARD while loading content and doing background
     * work.
     */
    public static final int STATE_BUBBLE = 1;

    /**
     * In this state the service shows UI and listens for
     * touches in the whole screen. The User has to click
     * any of the navigation buttons or any close button in the
     * UI to dismiss the UI and get back to interacting with other
     * apps
     */
    public static final int STATE_CARD = 2;

    @IntDef({STATE_WAITING, STATE_BUBBLE, STATE_CARD})
    public @interface UIState {
    }

    private @UIState
    int mState = STATE_WAITING;

    private WindowManager mWindowManager;

    private Bundle mSavedInstanceState;

    private View mBubbleView;

    private @LayoutRes int mBubbleViewResId;

    private View mCardView;

    private @LayoutRes int mCardViewResId;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @UIState
    public int getState(){
        return mState;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mSavedInstanceState = new Bundle();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getState() != STATE_WAITING) {
            goToState(STATE_WAITING);
        }
    }

    protected View getBubbleView(){
        return mBubbleView;
    }

    protected View getCardView(){
        return mCardView;
    }

    protected void goToState(@UIState int state){
        switch (mState){
            case STATE_WAITING:
                goToStateFromWaiting(state);
                break;
            case STATE_BUBBLE:
                goToStateFromBubble(state);
                break;
            case STATE_CARD:
                goToStateFromCard(state);
                break;
            default:
                break;
        }
        mState = state;
    }

    protected void onCreateBubble(){
    }

    @Nullable
    protected View onCreateBubbleView(){
        return null;
    }

    protected void onBubbleCreated(View bubbleView){
    }

    protected void setBubbleView(@LayoutRes int bubbleViewResId){
        mBubbleViewResId = bubbleViewResId;
        mBubbleView = LayoutInflater.from(this).inflate(mBubbleViewResId, getFakeRoot(), false);
    }

    protected void onCreateCard(){
    }

    @Nullable
    protected View onCreateCardView(){
        return null;
    }

    protected void onCardCreated(View cardView){
    }

    protected void setCardView(@LayoutRes int cardViewResId){
        mCardViewResId = cardViewResId;
        mCardView = LayoutInflater.from(this).inflate(mCardViewResId, getFakeRoot(), false);
    }

    protected void onResumeCard(@NonNull Bundle savedInstaceState){
    }

    protected void onHideCard(@NonNull Bundle savedInstanceState){
    }

    @NonNull
    protected LayoutParams getBubbleParams(){
        LayoutParams params = new LayoutParams();
        params.type = LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = params.flags | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_SPLIT_TOUCH;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.width = LayoutParams.WRAP_CONTENT;
        params.height = LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;

        return params;
    }

    @NonNull
    protected LayoutParams getCardParams(){
        LayoutParams params = new LayoutParams();
        params.type = LayoutParams.TYPE_PRIORITY_PHONE;
        params.flags = params.flags | LayoutParams.FLAG_DIM_BEHIND;
        params.dimAmount = 0.5f;
        params.gravity = Gravity.TOP;
        params.format = PixelFormat.TRANSLUCENT;

        return params;
    }

    private void goToStateFromWaiting(@UIState int state){
        switch (state){
            case STATE_WAITING:
                LogUtils.LOGD(TAG, "Already in waiting state");
                break;
            case STATE_BUBBLE:
                addBubble();
                LogUtils.LOGD(TAG, "To bubble state");
                break;
            case STATE_CARD:
                addBubble();
                addCard();
                LogUtils.LOGD(TAG, "To card state");
                break;
            default:
                LogUtils.LOGE(TAG, "Unknown state:" + state);
                break;
        }
    }

    private void goToStateFromBubble(@UIState int state){
        switch (state) {
            case STATE_WAITING:
                removeBubble();
                LogUtils.LOGD(TAG, "To waiting state");
                break;
            case STATE_BUBBLE:
                LogUtils.LOGD(TAG, "Already in bubble state");
                break;
            case STATE_CARD:
                addCard();
                LogUtils.LOGD(TAG, "To card state");
                break;
            default:
                LogUtils.LOGE(TAG, "Unknown state:" + state);
                break;
        }
    }

    private void goToStateFromCard(@UIState int state){
        switch (state){
            case STATE_WAITING:
                removeCard();
                removeBubble();
                LogUtils.LOGD(TAG, "To waiting state");
                break;
            case STATE_BUBBLE:
                removeCard();
                LogUtils.LOGD(TAG, "To bubble state");
                break;
            case STATE_CARD:
                LogUtils.LOGD(TAG, "Already in card state");
                break;
            default:
                LogUtils.LOGE(TAG, "Unknown state:" + state);
                break;
        }
    }

    private void addBubble(){
        onCreateBubble();
        View bubbleView = onCreateBubbleView();
        if(bubbleView != null) {
            mBubbleView = bubbleView;
        }

        if(mBubbleView == null) {
            return;
        }

        LayoutParams bubbleParams = getBubbleParams();
        mWindowManager.addView(mBubbleView, bubbleParams);
        onBubbleCreated(mBubbleView);
    }

    private void addCard(){
        onCreateCard();
        View cardView = onCreateCardView();
        if(cardView != null) {
            mCardView = cardView;
        }

        if(mCardView == null) {
            //TODO: throw an exception?
            return;
        }

        LayoutParams cardParams = getCardParams();
        mWindowManager.addView(mCardView, cardParams);
        mCardView.requestFocus();
        onCardCreated(mCardView);
        onResumeCard(mSavedInstanceState);
    }

    private void removeCard(){
        onHideCard(mSavedInstanceState);
        mWindowManager.removeView(mCardView);
    }

    private void removeBubble(){
        mWindowManager.removeView(mBubbleView);
    }

    @NonNull
    private ViewGroup getFakeRoot(){
        return new FrameLayout(this);
    }
}
