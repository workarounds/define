package in.workarounds.define.webviewDicts.livio.presenter;

import javax.inject.Inject;

import in.workarounds.define.portal.MainPortal;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;

/**
 * Created by madki on 13/10/15.
 */
@PerPortal
public class LivioEnglishPresenter extends LivioBasePresenter {
    private static final String TAG = LogUtils.makeLogTag(LivioEnglishPresenter.class);

    @Inject
    public LivioEnglishPresenter(LivioDictionary dictionary, MainPortal portal) {
        super(dictionary, portal);
    }

    @Override
    protected String getPackageName() {
        return LivioDictionary.PackageName.ENGLISH;
    }
}
