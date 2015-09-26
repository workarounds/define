package in.workarounds.define.portal;

import in.workarounds.define.ui.view.MeaningPage;

/**
 * Created by madki on 26/09/15.
 */
public interface ComponentProvider {
    PortalComponent component();
    void inject(MeaningPage meaningPage);
}
