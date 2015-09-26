package in.workarounds.define.portal;

import dagger.Component;
import in.workarounds.define.api.ApiModule;
import in.workarounds.define.dictionary.DictionaryModule;
import in.workarounds.define.file.FileModule;
import in.workarounds.define.network.NetworkComponent;
import in.workarounds.define.ui.view.MeaningPage;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(
        modules = {
                PortalModule.class,
                DictionaryModule.class,
                FileModule.class,
                ApiModule.class
        },
        dependencies = {
                NetworkComponent.class
        })
public interface PortalComponent {
    void inject(MainPortal portal);

    void inject(MeaningPage page);
}
