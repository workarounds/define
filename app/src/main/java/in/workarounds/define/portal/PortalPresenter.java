package in.workarounds.define.portal;

import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.view.swipeselect.SelectableTextView;

/**
 * Created by manidesto on 29/11/15.
 */
public interface PortalPresenter extends SelectableTextView.OnWordSelectedListener{
    void addMeaningPresenter(MeaningPresenter presenter);
    void removeMeaningPresenter(MeaningPresenter presenter);
    void onClipTextChanged(String clipText);
    void onCall();
    void onDefineClicked();
    void onSearchClicked();
    void onCopyClicked();
    void onWikiClicked();
    void onShareClicked();
    void onSettingsClicked();
    void finish();
    void finishWithNotification();
}
