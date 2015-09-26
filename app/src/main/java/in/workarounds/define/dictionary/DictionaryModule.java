package in.workarounds.define.dictionary;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.urban.Urban;
import in.workarounds.define.urban.UrbanDictionary;
import in.workarounds.define.wordnet.Wordnet;
import in.workarounds.define.wordnet.WordnetDictionary;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class DictionaryModule {

    @Provides @Wordnet @PerPortal
    public Dictionary wordnet(WordnetDictionary dictionary) {
        return dictionary;
    }

    @Provides @Urban @PerPortal
    public Dictionary urban(UrbanDictionary dictionary) {
        return dictionary;
    }
}
