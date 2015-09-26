package in.workarounds.define.wordnet;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.portal.PerPortal;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class WordnetModule {

    @Provides @PerPortal
    public Dictionary provideDictionary(WordnetDictionary dictionary) {
        return dictionary;
    }

}
