package in.workarounds.define.util;

public class StringUtils {

    /**
     * filters the word from anything other than alphabets
     *
     * @param word
     * @return
     */
    public static String preProcessWord(String word) {
        return word.replaceAll("[^a-zA-Z ]", "");
    }
}
