package in.workarounds.define.base;

import android.support.annotation.IntDef;

/**
 * Created by madki on 29/09/15.
 */
public class DictionaryException extends Exception {
    public static final int DICTIONARY_NOT_FOUND = 1;
    public static final int NETWORK_ERROR        = 2;
    public static final int UNKNOWN              = 3;

    private final int type;
    private final String message;

    public DictionaryException(@DictionaryExceptionType int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @IntDef({DICTIONARY_NOT_FOUND, NETWORK_ERROR, UNKNOWN})
    public static @interface DictionaryExceptionType {
    }
}
