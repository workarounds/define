package in.workarounds.define.urban;

import dagger.Component;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.network.NetworkComponent;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.meaning.MeaningPage;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(modules = {UrbanModule.class}, dependencies = {NetworkComponent.class})
public interface UrbanComponent {
    Dictionary dictionary();
    void inject(MeaningPage meaningPage);
}
