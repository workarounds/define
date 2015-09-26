package in.workarounds.define.meaning;

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
    private MeaningPage meaningPage;
    private String word;

    private boolean old;

    @Inject
    public MeaningPresenter(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void addView(MeaningPage view) {
        this.meaningPage = view;
        if(old) {
            restoreView();
        }
    }

    public void dropView() {
        this.meaningPage = null;
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

    private void restoreView() {
        meaningPage.title(word);
    }


}
