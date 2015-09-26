package in.workarounds.define.portal;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.ui.view.MeaningPresenter;
import in.workarounds.define.urban.Urban;
import in.workarounds.define.wordnet.Wordnet;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class PortalModule {

    @Provides @Wordnet @PerPortal
    public MeaningPresenter provideWordnetPresenter(@Wordnet Dictionary dictionary) {
        return new MeaningPresenter(dictionary);
    }

    @Provides @Urban @PerPortal
    public MeaningPresenter provideUrbanPresenter(@Urban Dictionary dictionary) {
        return new MeaningPresenter(dictionary);
    }
}
