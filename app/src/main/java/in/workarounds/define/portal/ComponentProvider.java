package in.workarounds.define.portal;

import in.workarounds.define.view.meaning.MeaningPage;

/**
 * Created by madki on 26/09/15.
 */
public interface ComponentProvider {
    PortalComponent component();
    void inject(MeaningPage meaningPage);
}
