package in.workarounds.define.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;

import java.text.BreakIterator;
import java.util.Locale;

import in.workarounds.define.util.LogUtils;
import in.workarounds.typography.TextView;

/**
 * Created by madki on 20/09/15.
 */
public class SelectableTextView extends TextView {
    private static final String TAG = LogUtils.makeLogTag(SelectableTextView.class);
    private BreakIterator mIterator = BreakIterator.getWordInstance(Locale.US);
    private OnWordSelectedListener mWordSelectedListener;

    public SelectableTextView(Context context) {
        super(context);
        init();
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setOnWordSelectedListener(OnWordSelectedListener wordClickListener) {
        this.mWordSelectedListener = wordClickListener;
    }

    public void removeOnWordSelectedListener() {
        this.mWordSelectedListener = null;
    }

    public void setSelectableText(String text) {
        if (text != null) {
            setText(text, BufferType.SPANNABLE);
            Spannable spans = (Spannable) getText();
            mIterator.setText(text);
            int start = mIterator.first();
            for (int end = mIterator.next(); end != BreakIterator.DONE; start = end, end = mIterator
                    .next()) {
                String possibleWord = text.substring(start, end);
                if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
                    spans.setSpan(clickSpan, start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private ClickableSpan getClickableSpan(final String word) {
        LogUtils.LOGD(TAG, "getClickableSpan called for word : " + word);
        return new ClickableSpan() {
            final String mWord;
            {
                mWord = word;
            }

            @Override
            public void onClick(View widget) {
                if(mWordSelectedListener != null) {
                    mWordSelectedListener.onWordSelected(mWord);
                }
            }

            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
            }
        };
    }

    public interface OnWordSelectedListener {
        void onWordSelected(String word);
    }

}
