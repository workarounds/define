package in.workarounds.define.webviewDicts.livio;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import javax.inject.Inject;

import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PackageManagerUtils;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class LivioDictionary {
    private static final String TAG = LogUtils.makeLogTag(LivioDictionary.class);
    public static final String providerName = "DictionaryProvider";

    private Context context;

    @Inject
    public LivioDictionary(Context context) {
        this.context = context;
    }


    public String results(String word, @PACKAGE_NAME String packageName) throws DictionaryException {
        if (!PackageManagerUtils.isAppInstalled(context, packageName)) {
            throw new DictionaryException(
                    DictionaryException.DICTIONARY_NOT_FOUND,
                    DefineApp.getContext().getString(R.string.exception_livio)
            );
        }
        String results = "";
        if (!TextUtils.isEmpty(word)) {
            results = getHtml(word, packageName);
        }
        return results;
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
