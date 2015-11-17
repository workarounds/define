package in.workarounds.define.webviewDicts;

import android.os.Handler;

import javax.inject.Inject;

import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.util.LogUtils;

/**
 * Created by Nithin on 13/10/15.
 */

public class JavaScriptInterface {
    private static final String TAG = LogUtils.makeLogTag(JavaScriptInterface.class);
    private MeaningPresenter presenter;
    private Handler handler;

    @Inject
    public JavaScriptInterface(MeaningPresenter presenter, Handler handler){
        this.presenter = presenter;
        this.handler = handler;
    }

    @android.webkit.JavascriptInterface
    public void loadMeaning(final String word){
        handler.post(new Runnable() {
            @Override
            public void run() {
                // code here will run on UI thread
                presenter.onWordUpdated(word.split(":")[1]);
            }
        });
    }
}
