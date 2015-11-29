package in.workarounds.define.portal;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.ui.activity.DashboardActivity;
import in.workarounds.define.ui.activity.UserPrefActivity;
import in.workarounds.define.util.LogUtils;

/**
 * Created by manidesto on 29/11/15.
 */
public class MainPortalPresenter implements PortalPresenter{
    private static final String TAG = "MainPortalPresenter";
    private PortalView portalView;
    private List<MeaningPresenter> meaningPresenters;
    private NotificationUtils notificationUtils;
    private Context context;

    private String clipText;
    private String selectedText;

    public MainPortalPresenter(PortalView portalView, NotificationUtils notificationUtils) {
        this.portalView = portalView;
        this.notificationUtils = notificationUtils;
        this.context = portalView.getContext();
        meaningPresenters = new ArrayList<>();
    }

    @Override
    public void addMeaningPresenter(MeaningPresenter presenter) {
        if(!meaningPresenters.contains(presenter)) {
            meaningPresenters.add(presenter);
            if(selectedText != null) {
                presenter.onWordUpdated(selectedText);
            }
        } else {
            LogUtils.LOGE(TAG, "Presenter already present");
        }
    }

    @Override
    public void removeMeaningPresenter(MeaningPresenter presenter) {
        if(meaningPresenters.contains(presenter)) {
            meaningPresenters.remove(presenter);
        } else {
            LogUtils.LOGE(TAG, "Presenter not added. Cannot remove");
        }
    }

    @Override
    public void onClipTextChanged(String clipText) {
        this.clipText = clipText.trim();
        portalView.setTextForSelection(this.clipText);
        if(isLessThanNWords(this.clipText, 3)) {
            portalView.selectAll();
        }
    }

    @Override
    public void onCall() {
        finishWithNotification();
    }

    @Override
    public void onDefineClicked() {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        portalView.hideAndFinish();
    }

    @Override
    public void onSearchClicked() {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, getTextInFocus());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        portalView.hideAndFinish();
    }

    @Override
    public void onCopyClicked() {
        if(selectedText != null) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Define", selectedText);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
        }
        portalView.hideAndFinish();
    }

    @Override
    public void onWikiClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://en.m.wikipedia.org/wiki/" + getTextInFocus()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        portalView.hideAndFinish();
    }

    @Override
    public void onShareClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getTextInFocus());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        startActivity(intent);
    }

    @Override
    public void onSettingsClicked() {
        Intent intent = new Intent(context, UserPrefActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onWordSelected(String word) {
        selectedText = word;
        portalView.showMeaningContainer();
        for(MeaningPresenter presenter : meaningPresenters) {
            presenter.onWordUpdated(word);
        }
    }

    @Override
    public void finish() {
        portalView.hideAndFinish();
    }

    @Override
    public void finishWithNotification() {
        notificationUtils.sendSilentBackupNotification(clipText);
        portalView.hideAndFinish();
    }

    private void startActivity(Intent intent) {
        context.startActivity(intent);
    }

    /**
     * Returns the selected text if any or the whole clipped text
     * @return selectedText if not null, clipText otherwise
     */
    private String getTextInFocus(){
        return selectedText == null ? clipText : selectedText;
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
