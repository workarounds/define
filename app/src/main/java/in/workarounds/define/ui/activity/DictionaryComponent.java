package in.workarounds.define.ui.activity;

import dagger.Component;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.portal.PortalModule;
import in.workarounds.define.webviewDicts.livio.LivioModule;
import in.workarounds.define.wordnet.WordnetModule;

/**
 * Created by Nithin on 29/10/15.
 */

@PerPortal
@Component(modules = {WordnetModule.class, LivioModule.class})
public interface DictionaryComponent {
    void inject(DictionariesActivity dictionariesActivity);
}

