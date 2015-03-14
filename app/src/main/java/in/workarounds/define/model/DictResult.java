package in.workarounds.define.model;

/**
 * result object of the dictionary, the following
 * are the properties of the object:
 * String word
 * String meaning
 * String type
 * String[] usage
 * String[] synonyms
 */
public class DictResult {
	private String word;
	private String meaning;
	private String[] synonyms;
	private String type;
	private String[] usage;

	public DictResult(String word, String meaning, String type, String[] usage,
                      String[] synonyms) {
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
		for (String syn : synonyms) {
			result += syn + ", ";
		}
		result += "\n";
		result += "Usage: ";
		for (String use : usage) {
			result += use + ", ";
		}
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

	public String[] getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(String[] synonyms) {
		this.synonyms = synonyms;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getUsage() {
		return usage;
	}

	public void setUsage(String[] usage) {
		this.usage = usage;
	}
}
