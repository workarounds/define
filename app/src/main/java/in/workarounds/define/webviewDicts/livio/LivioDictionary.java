package in.workarounds.define.webviewDicts.livio;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.HtmlDictionary;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class LivioDictionary implements HtmlDictionary {
    private static final String TAG = LogUtils.makeLogTag(LivioDictionary.class);
    public static String contentProvider = "livio.pack.lang.en_US.DictionaryProvider";
    public static Uri contentUri = Uri.parse("content://" + contentProvider + "/dictionary");

    private Context context;

    @Inject
    public LivioDictionary(Context context) {
        this.context = context;
    }

    @Override
    public String results(String word) throws DictionaryException {
        String results = "";
        if (!TextUtils.isEmpty(word)) {
            results = getHtml(word);
        }
        return results;
    }

    public String getHtml(String word){
        StringBuilder htmlBuilder = new StringBuilder();
        String html = "";
        //add css and js
        htmlBuilder = htmlBuilder
                .append("<HTML><HEAD><LINK href=\"css/livio.css\" type=\"text/css\" rel=\"stylesheet\"/>" +
                "<script src=\"js/livio.js\" type=\"text/javascript\" > </script>"+
                "</HEAD>");
        Cursor c = context.getContentResolver().query(contentUri, null, null, new String[]{word}, null);

        Document localObject1;
        if ((c != null) && c.moveToFirst())
        {
            for(String name : c.getColumnNames()) System.out.println(name);
            String htmlString  = c.getString(c.getColumnIndexOrThrow("suggest_text_2"));
            localObject1 = Jsoup.parse(htmlString);
            (localObject1).select("dl").remove();
            (localObject1).select("ul").remove();
            (localObject1).select("head").remove();

            for( Element element : localObject1.select("silence") ) {
                element.remove();
            }
            for(Element element : localObject1.select("hr")) {
                element.remove();
            }
            htmlBuilder.append(localObject1.html());
            html = htmlBuilder.toString();
            System.out.print(html);

            c.close();
        }
        return html;
    }
}
