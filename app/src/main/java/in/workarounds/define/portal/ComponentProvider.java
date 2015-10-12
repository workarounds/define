package in.workarounds.define.portal;

import in.workarounds.define.wordnet.WordnetMeaningPage;

/**
 * Created by madki on 26/09/15.
 */
public interface ComponentProvider {
    PortalComponent component();
    void inject(WordnetMeaningPage wordnetMeaningPage);
}
