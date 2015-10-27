package in.workarounds.define.constants;

import android.support.annotation.IntDef;

/**
 * Created by madki on 26/10/15.
 */
public interface DictionaryId {
    int WORDNET = 0;
    int LIVIO   = 1;
    int URBAN   = 2;

    int[] defaultOrder = {WORDNET, LIVIO, URBAN};
    boolean[] defaultVisibility = {true, true, true};

    @IntDef({WORDNET, LIVIO, URBAN})
    @interface DictId {
    }
}
