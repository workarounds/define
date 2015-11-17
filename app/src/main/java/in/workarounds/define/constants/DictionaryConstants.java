package in.workarounds.define.constants;

import android.support.annotation.IntDef;

/**
 * Created by madki on 26/10/15.
 */
public interface DictionaryConstants {
    int WORDNET = 0;
    int LIVIO_EN = 1;
    int URBAN = 2;
    int LIVIO_FR = 3;
    int LIVIO_IT = 4;
    int LIVIO_ES = 5;
    int LIVIO_DE = 6;

    int[] defaultOrder = {WORDNET, LIVIO_EN, URBAN};

    int[] allIds = {WORDNET, LIVIO_EN, URBAN, LIVIO_FR, LIVIO_IT, LIVIO_ES, LIVIO_DE};
    String[] dictNames = {"Wordnet", "English", "Urban", "French", "Italian", "Spanish", "German"};


    @IntDef({WORDNET, LIVIO_EN, URBAN, LIVIO_DE, LIVIO_ES, LIVIO_FR, LIVIO_IT})
    @interface DictId {
    }
}
