package in.workarounds.define.webviewDicts.livio;

import android.content.Context;
import android.support.annotation.StringRes;

import java.util.Arrays;
import java.util.List;

import in.workarounds.define.R;

/**
 * Created by madki on 27/01/16.
 */
public final class LivioLanguages {

    private LivioLanguages() {
    }

    public static class Language {

        private final String name;
        private final String description;
        private final String packageName;
        private final String installPrompt;

        private Language(@StringRes int name, @StringRes int description,
                         String packageName, @StringRes int installPrompt, Context context) {

            this.name = context.getString(name);
            this.description = context.getString(description);
            this.packageName = packageName;
            this.installPrompt = context.getString(installPrompt);
        }

        public String name() {
            return name;
        }

        public String description() {
            return description;
        }

        @LivioDictionary.PACKAGE_NAME
        public String packageName() {
            return packageName;
        }

        public String installPrompt() {
            return installPrompt;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof Language)) {
                return false;
            } else {
                Language language = (Language) o;
                return packageName.equals(language.packageName);
            }
        }
    }

    public static List<Language> all(Context context) {
        return Arrays.asList(
                english(context),
                french(context),
                german(context),
                italian(context),
                spanish(context)
        );
    }

    public static Language english(Context context) {
        return new Language(
                R.string.lv_english_name,
                R.string.lv_english_description,
                LivioDictionary.PackageName.ENGLISH,
                R.string.lv_english_install,
                context
        );
    }

    public static Language french(Context context) {
        return new Language(
                R.string.lv_french_name,
                R.string.lv_french_description,
                LivioDictionary.PackageName.FRENCH,
                R.string.lv_french_install,
                context
        );
    }

    public static Language german(Context context) {
        return new Language(
                R.string.lv_german_name,
                R.string.lv_german_description,
                LivioDictionary.PackageName.GERMAN,
                R.string.lv_german_install,
                context
        );
    }

    public static Language italian(Context context) {
        return new Language(
                R.string.lv_italian_name,
                R.string.lv_italian_description,
                LivioDictionary.PackageName.ITALIAN,
                R.string.lv_italian_install,
                context
        );
    }

    public static Language spanish(Context context) {
        return new Language(
                R.string.lv_spanish_name,
                R.string.lv_spanish_description,
                LivioDictionary.PackageName.SPANISH,
                R.string.lv_spanish_install,
                context
        );
    }


}
