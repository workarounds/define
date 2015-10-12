package in.workarounds.define.util;

public class StringUtils {

    /**
     * filters the onWordUpdated from anything other than alphabets
     *
     * @param word
     * @return
     */
    public static String preProcessWord(String word) {
        return word.replaceAll("[^a-zA-Z ]", "");
    }

    public static String makeFirstLetterLowerCase(String word){
        return word.substring(0,1).toLowerCase() + word.substring(1);
    }

    public static String makeFirstLetterUpperCase(String word){
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }
}
