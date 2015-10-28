package in.workarounds.define.portal;

import dagger.Component;
import in.workarounds.define.network.NetworkComponent;
import in.workarounds.define.urban.UrbanMeaningPage;
import in.workarounds.define.urban.UrbanModule;
import in.workarounds.define.webviewDicts.livio.LivioMeaningPage;
import in.workarounds.define.webviewDicts.livio.LivioModule;
import in.workarounds.define.wordnet.WordnetMeaningPage;
import in.workarounds.define.wordnet.WordnetModule;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(modules = {PortalModule.class, UrbanModule.class, WordnetModule.class, LivioModule.class}
        , dependencies = {NetworkComponent.class})
public interface PortalComponent {
    void inject(LivioMeaningPage livioMeaningPage);
    void inject(UrbanMeaningPage urbanMeaningPage);
    void inject(WordnetMeaningPage wordnetMeaningPage);
}
