package in.workarounds.define.base;

import java.util.List;

import edu.smu.tspell.wordnet.Synset;

/**
 * Created by Nithin on 13/10/15.
 */
public interface IWordnetDictionary {
    List<Synset> results(String word) throws DictionaryException;
}
