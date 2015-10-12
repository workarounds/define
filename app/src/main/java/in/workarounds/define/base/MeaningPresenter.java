package in.workarounds.define.base;

import android.view.View;

/**
 * Created by madki on 13/10/15.
 */
public interface MeaningPresenter {

    void addView(View view);
    void dropView();
    void onWordUpdated(String word);

}
