package in.workarounds.define.ui.view;

import android.view.View;

import javax.inject.Inject;

import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class MeaningPresenter {
    private static final String TAG = LogUtils.makeLogTag(MeaningPresenter.class);

    private Dictionary dictionary;
    private View view;
    private String word;

    private boolean old;

    @Inject
    public MeaningPresenter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void addView(View view) {
        this.view = view;
    }

    public void dropView() {
        this.view = null;
        old = true;
    }

    public void word(String word) {
        this.word = word;
        LogUtils.LOGD(TAG, "word set in presenter : " + word);
    }

    public String word() {
        return word;
    }

    public boolean old() {
        return old;
    }

}
