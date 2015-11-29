package in.workarounds.define.portal;

import in.workarounds.define.base.MeaningPresenter;

/**
 * Created by manidesto on 30/11/15.
 */
public interface MeaningsController {
    void onWordUpdated(String word);
    void addMeaningPresenter(MeaningPresenter presenter);
    void removeMeaningPresenter(MeaningPresenter presenter);
    void onInstallClicked();
    void onDownloadClicked();
}
