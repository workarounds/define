package in.workarounds.define.webviewDicts.livio;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import javax.inject.Inject;

import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.PackageManagerUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class LivioDictionary {
    public static final String providerName = "DictionaryProvider";

    private Context context;

    @Inject
    public LivioDictionary(Context context) {
        this.context = context;
    }


    public String results(String word, LivioLanguages.Language language) throws DictionaryException {
        if (!PackageManagerUtils.isAppInstalled(context, language.packageName())) {
            throw new DictionaryException(
                    DictionaryException.DICTIONARY_NOT_FOUND,
                    language.installPrompt()
            );
        }
        String results = "";
        if (!TextUtils.isEmpty(word)) {
            results = getHtml(word, language.packageName());
        }
        return results;
    }

    public Observable<String> resultsObservable(String word, LivioLanguages.Language language) {
        return Observable.fromCallable(() -> results(word, language))
                .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR));
    }

    private Uri getContentUri(@PACKAGE_NAME String packageName) {
        return Uri.parse("content://" + packageName + "." + providerName + "/dictionary");
    }

    public String getHtml(String word, @PACKAGE_NAME String packageName) {
        StringBuilder htmlBuilder = new StringBuilder();
        String html = "";
        //add css and js
        htmlBuilder = htmlBuilder
                .append("<HTML><HEAD><LINK href=\"css/livio.css\" type=\"text/css\" rel=\"stylesheet\"/>" +
                        "<script src=\"js/livio.js\" type=\"text/javascript\" > </script>" +
                        "</HEAD>");
        Cursor c = context.getContentResolver().query(getContentUri(packageName), null, null, new String[]{word}, null);

        Document localObject1;
        if ((c != null) && c.moveToFirst()) {
            for (String name : c.getColumnNames()) System.out.println(name);
            String htmlString = c.getString(c.getColumnIndexOrThrow("suggest_text_2"));
            localObject1 = Jsoup.parse(htmlString);
            //(localObject1).select("dl").remove();
            (localObject1).select("head").remove();

            for (Element element : localObject1.select("silence")) {
                element.remove();
            }
            for (Element element : localObject1.select("hr")) {
                element.remove();
            }

            Elements elementsByClass = (localObject1).getElementsByClass("head");
            if (elementsByClass != null) {
                for (Element etymologyElement : elementsByClass) {
                    if (etymologyElement.text().equals("etymology")) {
                        Element etymologyContentElement = etymologyElement.nextElementSibling();
                        if (etymologyContentElement != null && !etymologyContentElement.tag().equals(Tag.valueOf("span"))) {
                            etymologyContentElement.remove();
                        }
                        etymologyElement.remove(); //remove etymology
                    }
                }
            }

            htmlBuilder.append(localObject1.html());
            html = htmlBuilder.toString();
            c.close();
        }
        return html;
    }

    public interface PackageName {
        String ENGLISH = "livio.pack.lang.en_US";
        String FRENCH = "livio.pack.lang.fr_FR";
        String ITALIAN = "livio.pack.lang.it_IT";
        String SPANISH = "livio.pack.lang.es_ES";
        String GERMAN = "livio.pack.lang.de_DE";
    }

    @StringDef({PackageName.ENGLISH, PackageName.FRENCH, PackageName.ITALIAN,
            PackageName.SPANISH, PackageName.GERMAN})
    public @interface PACKAGE_NAME {
    }
}
