package in.workarounds.define.webviewDicts.livio.presenter;

import javax.inject.Inject;

import in.workarounds.define.DefineApp;
import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;
import in.workarounds.define.webviewDicts.livio.LivioLanguages;

/**
 * Created by madki on 07/11/15.
 */
@PerPortal
public class LivioFrenchPresenter extends LivioBasePresenter {

    @Inject
    public LivioFrenchPresenter(LivioDictionary dictionary, MeaningsController controller, ContextHelper contextHelper) {
        super(dictionary, controller, contextHelper);
    }

    @Override
    protected LivioLanguages.Language getLanguage() {
        return LivioLanguages.french(DefineApp.getContext());
    }

}
