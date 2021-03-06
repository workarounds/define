package in.workarounds.define.ui.view.SelectionCard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.BreakIterator;

import javax.inject.Inject;

import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.ui.view.swipeselect.SelectableTextView;
import timber.log.Timber;

/**
 * Created by manidesto on 30/11/15.
 */
@PerPortal
public class SelectionCardPresenter implements SelectableTextView.OnWordSelectedListener {
    String clipText;
    String selected;
    @Nullable
    SelectionCardView selectionCardView;
    SelectionCardController controller;
    ContextHelper contextHelper;

    @Inject
    public SelectionCardPresenter(SelectionCardController controller, ContextHelper contextHelper) {
        this.controller = controller;
        this.contextHelper = contextHelper;
        Timber.d("new SelectionCardPresenter");
    }

    @Override
    public void onWordSelected(String word) {
        selected = word;
        controller.onWordSelected(word);
    }

    public void onClipTextChanged(@NonNull String clipText) {
        this.selected = null;
        setClipText(clipText);
    }

    private void setClipText(@NonNull String clipText) {
        this.clipText = clipText.trim();
        if (selectionCardView != null) {
            selectionCardView.setTextForSelection(this.clipText);
            if (isLessThanNWords(this.clipText, 3)) {
                selectionCardView.selectAll();
            }
        }
    }

    public void addView(SelectionCardView selectionCardView) {
        this.selectionCardView = selectionCardView;
        if (clipText != null) {
            setClipText(clipText);
        }
        if (selected != null) {
           selectionCardView.selectText(selected);
        }
    }

    public void removeView() {
        this.selectionCardView = null;
    }

    public void onDefineClicked() {
        contextHelper.openDefineApp();
        controller.onButtonClicked();
    }

    public void onSearchClicked() {
        contextHelper.searchWeb(getTextInFocus());
        controller.onButtonClicked();
    }

    public void onCopyClicked() {
        if (selected != null) {
            contextHelper.copyToClipboard(selected);
        }
        controller.onButtonClicked();
    }

    public void onWikiClicked() {
        contextHelper.searchWiki(getTextInFocus());
        controller.onButtonClicked();
    }

    public void onShareClicked() {
        contextHelper.sharePlainText(getTextInFocus());
        controller.onButtonClicked();
    }

    public void onSettingsClicked() {
        contextHelper.openSettings();
        controller.onButtonClicked();
    }

    /**
     * Returns the selected text if any or the whole clipped text
     *
     * @return selectedText if not null, clipText otherwise
     */
    private String getTextInFocus() {
        return selected == null ? clipText : selected;
    }

    public static boolean isLessThanNWords(String text, int n) {
        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(text);
        iterator.first();
        int count = 0;
        for (int end = iterator.next(); end != BreakIterator.DONE; end = iterator.next()) {
            if (++count > n) return false;
        }
        return true;
    }
}
