package in.workarounds.define.helper;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 14/05/15.
 */
public class SpeechHelper {
    private static final String TAG = LogUtils.makeLogTag(SpeechHelper.class);
    private static final String TTS_ID = "selected_word";

    private Context mContext;
    private TextToSpeech mTextToSpeech;

    /**
     * constructor. Initializes the TextToSpeech Object
     * Do this in onCreate as initialising TextToSpeech object takes time
     * make sure to call the destroy method of Speech Helper in the
     * onDestroy of activity or service that uses this
     * @param context context
     */
    public SpeechHelper(Context context) {
        mContext = context;
        //TODO check if TTS service is installed else show installation screen !
        mTextToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    /**
     * helper method to speak a word in American pronunciation
     * @param word to be pronounced
     */
    public void speakAmerican(String word) {
        speak(word, Locale.US);
    }

    /**
     * helper method to speak a word in British pronunciation
     * @param word to be pronounced
     */
    public void speakBritish(String word) {
        speak(word, Locale.UK);
    }

    /**
     * sets the given Locale of the TextToSpeech object
     * and speaks the word
     * @param word to be pronounced
     * @param locale locale to be used
     */
    protected void speak(String word, Locale locale) {
        if(mTextToSpeech !=null) {
            mTextToSpeech.setLanguage(locale);
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                mTextToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null, TTS_ID);
            } else {
                mTextToSpeech.speak(word, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    /**
     * stops and shuts down the TextToSpeech object
     * this releases the resources that the TextToSpeech object holds
     * call this method in the onDestroy of the activity using it
     */
    public void destroy() {
        if(mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

}
