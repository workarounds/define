package in.workarounds.define.wordnet;

import dagger.Component;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.view.meaning.MeaningPage;
import in.workarounds.define.view.meaning.MeaningPresenter;
import in.workarounds.define.portal.PerPortal;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(modules = {WordnetModule.class})
public interface WordnetComponent {
    Dictionary dictionary();
    MeaningPresenter presenter();
    void inject(MeaningPage meaningPage);
}
