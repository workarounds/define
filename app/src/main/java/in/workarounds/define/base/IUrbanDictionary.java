package in.workarounds.define.base;

import in.workarounds.define.urban.UrbanResult;

/**
 * Created by Nithin on 13/10/15.
 */
public interface IUrbanDictionary {
    UrbanResult results(String word) throws DictionaryException;
}
