package in.workarounds.define.urban;

import dagger.Component;
import in.workarounds.define.base.Dictionary;
import in.workarounds.define.wordnet.WordnetPresenter;
import in.workarounds.define.network.NetworkComponent;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.wordnet.WordnetMeaningPage;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(modules = {UrbanModule.class}, dependencies = {NetworkComponent.class})
public interface UrbanComponent {
    Dictionary dictionary();
    WordnetPresenter presenter();
    void inject(WordnetMeaningPage wordnetMeaningPage);
}
