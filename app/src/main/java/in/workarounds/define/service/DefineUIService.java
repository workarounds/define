package in.workarounds.define.service;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

import in.workarounds.define.R;
import in.workarounds.define.model.DictResult;
import in.workarounds.define.model.Dictionary;
import in.workarounds.define.model.WordnetDictionary;
import in.workarounds.define.ui.adapter.DefineCardHandler;
import in.workarounds.define.ui.view.PopupRoot;

/**
 * Created by manidesto on 15/05/15.
 */
public class DefineUIService extends UIService implements PopupRoot.OnCloseDialogsListener, DefineCardHandler.SelectedTextChangedListener{
    public static final String INTENT_EXTRA_CLIPTEXT = "intent_clip_text";
    private DefineCardHandler mCardHandler;
    private String mClipText;
    private Dictionary mDictionary;
    private String mWordForm;
    private ArrayList<DictResult> mResultsList;
    private boolean mResultListUpdated = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mDictionary = new WordnetDictionary(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        goToState(STATE_BUBBLE);
        mClipText = intent.getStringExtra(INTENT_EXTRA_CLIPTEXT);
        return START_NOT_STICKY;
    }

    @Override
    protected void onCreateBubble() {
        super.onCreateBubble();
        setBubbleView(R.layout.layout_test_bubble);
    }

    @Override
    protected void onBubbleCreated(View bubbleView) {
        super.onBubbleCreated(bubbleView);
        bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToState(STATE_CARD);
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

    @Override
    protected void onCreateCard() {
        super.onCreateCard();
        setCardView(R.layout.card_define_service);
    }

    @Override
    protected void onCardCreated(View cardView) {
        super.onCardCreated(cardView);
        initCard(cardView);
        if(mResultListUpdated){
            mCardHandler.showMeanings(mWordForm, mResultsList);
            mResultListUpdated = false;
        } else {
            handleClipText(mClipText);
        }
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
    private void initCard(View root) {
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

        PopupRoot popupRoot = (PopupRoot) popup;
        popupRoot.registerOnCloseDialogsListener(this);
    }

    /**
     * Function that is called by the ClipboardService when a text is copied
     *
     * @param clipText
     *            text that is copied into the clipboard
     */
    public void handleClipText(String clipText) {
        String[] words = clipText.split(" ");

        if (words.length > 1) {
            mCardHandler.showBubbles(words);
        } else {
            new MeaningsTask().execute(words[0]);
        }
    }

    @Override
    public void onCloseDialogs() {
        goToState(STATE_WAITING);
        stopSelf();
    }

    @Override
    public void onSelectedTextChanged(String selectedText) {
        if(!selectedText.equals(mWordForm)) {
            new MeaningsTask().execute(selectedText);
        }
    }

    private void onResultListUpdated(String wordForm, ArrayList<DictResult> results){
        mWordForm = wordForm;
        mResultsList = results;
        if(getState() == STATE_CARD){
            mCardHandler.showMeanings(wordForm, results);
        } else {
            mResultListUpdated = true;
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
}
