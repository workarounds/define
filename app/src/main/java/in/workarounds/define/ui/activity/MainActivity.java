package in.workarounds.define.ui.activity;

import android.os.Bundle;
import android.view.View;

import in.workarounds.define.R;
import in.workarounds.define.helper.SpeechHelper;
import in.workarounds.define.util.LogUtils;


public class MainActivity extends BaseActivity {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    /**
     * Speech helper that starts and manages the TTS object
     */
    private SpeechHelper mSpeechHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeechHelper = new SpeechHelper(this);


    }

    @Override
    protected String getToolbarTitle() {
        return "Define";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechHelper.destroy();
    }

    public void speakAmerican(View v) {
        mSpeechHelper.speakAmerican("selected onWordUpdated");
    }

    public void speakBritish(View v) {
        mSpeechHelper.speakBritish("selected onWordUpdated");
    }


}
