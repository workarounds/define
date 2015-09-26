package in.workarounds.define.urban;

import java.util.ArrayList;

import javax.inject.Inject;

import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.dictionary.Result;
import in.workarounds.define.portal.PerPortal;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class UrbanDictionary implements Dictionary {

    @Inject
    public UrbanDictionary(UrbanApi api) {

    }

    @Override
    public ArrayList<Result> results(String word) {
        return null;
    }
}
