package in.workarounds.define.helper;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import in.workarounds.define.ui.activity.DashboardActivity;
import in.workarounds.define.ui.activity.UserPrefActivity;

/**
 * Created by manidesto on 30/11/15.
 */
public class ContextHelper {
    Context context;
    boolean startNewTask;

    public ContextHelper(Context context) {
        this(context, false);
    }

    public ContextHelper(Context context, boolean startNewTask) {
        this.context = context;
        this.startNewTask = startNewTask;
    }

    public void openDefineApp() {
        Intent intent = new Intent(context, DashboardActivity.class);
        startActivity(intent);
    }

    public void searchWeb(String text) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, text);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Define", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
    }

    public void searchWiki(String text) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.m.wikipedia.org/wiki/" + text));
        startActivity(intent);
    }

    public void sharePlainText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(context, UserPrefActivity.class);
        startActivity(intent);
    }

    private void startActivity (Intent intent) {
        if(startNewTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
