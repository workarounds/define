package in.workarounds.define.base;

import java.util.List;

/**
 * Created by madki on 25/09/15.
 */
public interface Dictionary{
    List<Result> results(String word) throws DictionaryException;
}
