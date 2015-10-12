package in.workarounds.define.base;

import java.util.List;

/**
 * Created by madki on 25/09/15.
 */
public class Result {
    public static final String TYPE_NOUN = "noun";
    public static final String TYPE_VERB = "verb";
    public static final String TYPE_ADJ  = "adjective";
    public static final String TYPE_ADV  = "adverb";
    public static final String TYPE_ADJS = "adj. satellite";
    public static final String TYPE_NONE = "unkonwn";

    protected String word;
    protected String definition;
    protected List<String> synonyms;
    protected String type;
    protected List<String> usages;

    public String word() {
        return word;
    }

    public void word(String word) {
        this.word = word;
    }

    public String definition() {
        return definition;
    }

    public void definition(String meaning) {
        this.definition = meaning;
    }

    public List<String> synonyms() {
        return synonyms;
    }

    public void synonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String type() {
        return type;
    }

    public void type(String type) {
        this.type = type;
    }

    public List<String> usages() {
        return usages;
    }

    public void usages(List<String> usages) {
        this.usages = usages;
    }
}
