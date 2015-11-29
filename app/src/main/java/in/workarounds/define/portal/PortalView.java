package in.workarounds.define.portal;

import android.content.Context;

/**
 * Created by manidesto on 29/11/15.
 */
public interface PortalView {
    void hideAndFinish();
    void showMeaningContainer();
    void setTextForSelection(String text);
    void selectAll();
    Context getContext();
}
