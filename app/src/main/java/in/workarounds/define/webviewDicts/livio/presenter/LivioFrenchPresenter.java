package in.workarounds.define.webviewDicts.livio.presenter;

import javax.inject.Inject;

import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;

/**
 * Created by madki on 07/11/15.
 */
@PerPortal
public class LivioFrenchPresenter extends LivioBasePresenter {

    @Inject
    public LivioFrenchPresenter(LivioDictionary dictionary, MeaningsController controller) {
        super(dictionary, controller);
    }

    @Override
    protected String getPackageName() {
        return LivioDictionary.PackageName.FRENCH;
    }
}
