package in.workarounds.define.base;

import android.view.View;

/**
 * Created by madki on 13/10/15.
 */
public interface MeaningPresenter {
    String NO_WORD_SELECTED_ERROR = "Please select a word to define. Tap on a word to select one. Or swipe to select multiple words";
    String NO_RESULTS_FOUND_ERROR = "Sorry, no results found";
    String UNKNOWN_ERROR = "Sorry, something went wrong";

    void addView(View view);
    void dropView();
    void onWordUpdated(String word);

}
