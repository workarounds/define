package in.workarounds.define.wordnet;

import dagger.Component;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.meaning.MeaningPage;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(modules = {WordnetModule.class})
public interface WordnetComponent {
    Dictionary dictionary();
    void inject(MeaningPage meaningPage);
}
