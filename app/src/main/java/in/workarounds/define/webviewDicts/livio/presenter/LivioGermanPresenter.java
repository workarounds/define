package in.workarounds.define.webviewDicts.livio.presenter;

import javax.inject.Inject;

import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;

/**
 * Created by madki on 07/11/15.
 */
@PerPortal
public class LivioGermanPresenter extends LivioBasePresenter {

    @Inject
    public LivioGermanPresenter(LivioDictionary dictionary, MeaningsController controller, ContextHelper contextHelper) {
        super(dictionary, controller, contextHelper);
    }

    @Override
    protected String getPackageName() {
        return LivioDictionary.PackageName.GERMAN;
    }
}
