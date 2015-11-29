package in.workarounds.define.view.SelectionCard;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.text.BreakIterator;

import javax.inject.Inject;

import in.workarounds.define.ui.activity.DashboardActivity;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.view.swipeselect.SelectableTextView;

/**
 * Created by manidesto on 30/11/15.
 */
public class SelectionCardPresenter implements SelectableTextView.OnWordSelectedListener{
    String clipText;
    String selected;
    @Nullable SelectionCardView selectionCardView;
    SelectionCardListener listener;

    @Inject
    public SelectionCardPresenter(SelectionCardListener listener) {
        this.listener = listener;
    }

    @Override
    public void onWordSelected(String word) {
        selected = word;
        listener.onWordSelected(word);
    }

    public void onClipTextChanged(@NonNull String clipText) {
        this.clipText = clipText.trim();
        if(selectionCardView != null) {
            selectionCardView.setTextForSelection(this.clipText);
            if (isLessThanNWords(this.clipText, 3)) {
                selectionCardView.selectAll();
            }
        }
    }

    public void addView(SelectionCardView selectionCardView) {
        this.selectionCardView = selectionCardView;
        if(clipText != null) {
            onClipTextChanged(clipText);
        }
    }

    public void removeView() {
        this.selectionCardView = null;
    }

    public void onDefineClicked() {
        Context context = getContext();
        if(context != null) {
            Intent intent = new Intent(context, DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        listener.onButtonClicked();
    }

    public void onSearchClicked() {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, getTextInFocus());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        listener.onButtonClicked();
    }

    public void onCopyClicked() {
        Context context = getContext();
        if(selected != null && context != null) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Define", selected);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
        }
        listener.onButtonClicked();
    }

    public void onWikiClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.m.wikipedia.org/wiki/" + getTextInFocus()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        listener.onButtonClicked();
    }

    public void onShareClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getTextInFocus());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        startActivity(intent);
        listener.onButtonClicked();
    }

    public void onSettingsClicked() {
        Intent intent = new Intent(getContext(), UserPrefActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        listener.onButtonClicked();
    }

    private void startActivity(Intent intent) {
        if(selectionCardView != null) {
            selectionCardView.getContext().startActivity(intent);
        }
    }

    private Context getContext() {
        if(selectionCardView != null) {
            return selectionCardView.getContext();
        } else {
            return null;
        }
    }

    /**
     * Returns the selected text if any or the whole clipped text
     * @return selectedText if not null, clipText otherwise
     */
    private String getTextInFocus(){
        return selected == null ? clipText : selected;
    }

    public static boolean isLessThanNWords(String text, int n) {
        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(text);
        iterator.first();
        int count = 0;
        for(int end = iterator.next(); end != BreakIterator.DONE; end = iterator.next()){
            count++;
        }
        return count <= n;
    }
}
