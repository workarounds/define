package in.workarounds.define.wordnet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by madki on 26/09/15.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Wordnet {
    String value() default "wordnet";
}
