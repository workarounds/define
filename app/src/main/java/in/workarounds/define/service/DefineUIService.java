package in.workarounds.define.service;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

import in.workarounds.define.R;
import in.workarounds.define.api.UrbanDictionaryClient;
import in.workarounds.define.handler.LifeHandler;
import in.workarounds.define.model.DictResult;
import in.workarounds.define.model.Dictionary;
import in.workarounds.define.model.WordnetDictionary;
import in.workarounds.define.model.urbandictionary.Term;
import in.workarounds.define.ui.adapter.DefineCardHandler;
import in.workarounds.define.ui.view.PopupRoot;
import in.workarounds.define.util.LogUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by manidesto on 15/05/15.
 */
public abstract class DefineUIService extends UIService implements PopupRoot.OnCloseDialogsListener, DefineCardHandler.SelectedTextChangedListener{
    public static final String INTENT_EXTRA_CLIPTEXT = "intent_clip_text";
    private static String TAG = LogUtils.makeLogTag(DefineUIService.class);
    protected LifeHandler mHandler;
    private DefineCardHandler mCardHandler;
    private String mClipText;
    private Dictionary mDictionary;
    private String mWordForm;
    private boolean mIgnoreIntent = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new LifeHandler(this);
        mDictionary = new WordnetDictionary(this);
        setBubbleView(R.layout.layout_test_bubble);
        setCardView(R.layout.card_define_service);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mIgnoreIntent){
            handleIntent(intent);
        } else {
            mIgnoreIntent = false;
        }
        return START_NOT_STICKY;
    }

    @Override
    protected void onBubbleCreated(View bubbleView) {
        super.onBubbleCreated(bubbleView);
        bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBubbleClicked(v);
            }
        });
    }

    @NonNull
    @Override
    protected WindowManager.LayoutParams getBubbleParams() {
        WindowManager.LayoutParams params = super.getBubbleParams();
        params.windowAnimations = R.style.BubbleAnimations;
        return params;
    }

    @NonNull
    @Override
    protected WindowManager.LayoutParams getCardParams() {
        WindowManager.LayoutParams params = super.getCardParams();
        params.windowAnimations = R.style.CardAnimations;
        return params;
    }

    /**
     * sets up touch listeners for the popup and card so that touch outside the
     * card dismisses the popup and touch on the card consumes the touch
     */
    protected void initCard(View root) {
        ViewGroup popup = (ViewGroup) root.findViewById(R.id.popup);

        popup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {
                int action = me.getAction();
                switch (action) {
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        onCloseDialogs();
                        v.performClick();
                    default:
                        break;
                }
                return true;
            }
        });
        mCardHandler = new DefineCardHandler(this, root, this);
        View copyButton = root
                .findViewById((R.id.iv_define_icon));
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clipText(mCardHandler.extractTextFromCard());
                onCloseDialogs();
            }
        });

        PopupRoot popupRoot = (PopupRoot) popup;
        popupRoot.registerOnCloseDialogsListener(this);
    }

    /**
     * Function that is called by the ClipboardService when a text is copied
     *
     * @param clipText
     *            text that is copied into the clipboard
     */
    public void handleClipText(@NonNull String clipText) {
        String[] words = clipText.split(" ");

        if (words.length > 1) {
            mCardHandler.showBubbles(words);
        } else {
            new MeaningsTask().execute(words[0]);
            getUrbanDictionaryMeanings(words[0]);
        }
    }

    @Override
    public void onCloseDialogs() {
        goToState(STATE_WAITING);
        int animTime = getResources().getInteger(R.integer.default_anim_time);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, animTime);
    }

    public void onStayAlive(){

    }

    public void onDieOut(){

    }

    protected void onBubbleClicked(View bubble){
        toggleCard();
    }

    @Override
    public void onSelectedTextChanged(String selectedText) {
        if(!selectedText.equals(mWordForm)) {
            new MeaningsTask().execute(selectedText);
            getUrbanDictionaryMeanings(selectedText);
        }
    }

    protected void clipText(String text) {
        mIgnoreIntent = true;
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("RECOPY", text);
        clipboardManager.setPrimaryClip(clip);
        // Give some feedback
    }

    protected abstract void handleIntent(Intent intent);

    @Nullable
    protected String getClipTextFromIntent(Intent intent){
        String clipText = intent.getStringExtra(INTENT_EXTRA_CLIPTEXT);
        if(clipText != null){
            mClipText = clipText;
        }
        return clipText;
    }

    protected void toggleCard(){
        if(getState() == STATE_CARD){
            goToState(STATE_BUBBLE);
        } else {
            goToState(STATE_CARD);
        }
    }

    private void showCard(String clipText){
        if(mCardHandler == null){
            initCard(getCardView());
            handleClipText(clipText);
        }
        goToState(STATE_CARD);
    }

    private void onResultListUpdated(String wordForm, ArrayList<DictResult> results){
        mWordForm = wordForm;
        mCardHandler.showMeanings(wordForm, results);
    }

    private void onUrbanDictResultListUpdated(String wordForm, Term results){

        if (mWordForm != null && mWordForm.equals(wordForm)) {
            mCardHandler.addUrbanDictMeaningsToScrollView(results);
        }
    }

    private class MeaningsTask extends AsyncTask<String, Integer, ArrayList<DictResult>> {
        private String wordForm;
        @Override
        protected ArrayList<DictResult> doInBackground(String... params) {
            wordForm = params[0];
            return mDictionary.getMeanings(wordForm);
        }

        @Override
        protected void onPostExecute(ArrayList<DictResult> results) {
            onResultListUpdated(wordForm, results);
        }
    }

    private void getUrbanDictionaryMeanings(final String wordForm) {

        if (!wordForm.isEmpty()) {

            UrbanDictionaryClient urbanDictionaryClient = new UrbanDictionaryClient();

            UrbanDictionaryClient.UrbanDictionaryService service = urbanDictionaryClient.service;

            service.term(wordForm, new Callback<Term>() {
                @Override
                public void success(Term term, Response response) {
                    onUrbanDictResultListUpdated(wordForm, term);
                }

                @Override
                public void failure(RetrofitError error) {
                    //Do nothing
                    LogUtils.LOGD(TAG, error.toString());
                }
            });
        }
    }


}
