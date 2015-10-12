package in.workarounds.define.model;

import android.text.TextUtils;

import java.util.List;

import in.workarounds.define.util.StringUtils;

/**
 * result object of the dictionary, the following
 * are the properties of the object:
 * String onWordUpdated
 * String definition
 * String type
 * List usage
 * List synonyms
 */
public class DictResult {
	private String word;
	private String meaning;
	private List<String> synonyms;
	private String type;
	private List<String> usage;

	public DictResult(String word, String meaning, String type,  List<String> usage,
                      List<String> synonyms) {
		this.word = word;
		this.meaning = meaning;
		this.usage = usage;
		this.synonyms = synonyms;
		this.type = type;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "Word: " + word + "\n";
		result += "Meaning: " + meaning + "\n";
		result += "Type: " + type + "\n";
		result += "Synonyms: ";
        result += TextUtils.join(", ",synonyms);
		result += "\n";
		result += "Usage: ";
        result += TextUtils.join(", ",usage);
		return result;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getMeaning() {
		return meaning;
	}

	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms( List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public  List<String> getUsage() {
		return usage;
	}

	public void setUsage( List<String> usage) {
		this.usage = usage;
	}
}
