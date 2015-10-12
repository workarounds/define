package in.workarounds.define.wordnet;

import dagger.Component;
import in.workarounds.define.base.Dictionary;
import in.workarounds.define.portal.PerPortal;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(modules = {WordnetModule.class})
public interface WordnetComponent {
    Dictionary dictionary();
    WordnetPresenter presenter();
    void inject(WordnetMeaningPage wordnetMeaningPage);
}
