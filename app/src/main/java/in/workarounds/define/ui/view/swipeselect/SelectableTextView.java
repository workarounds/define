package in.workarounds.define.ui.view.swipeselect;

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.util.AttributeSet;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import in.workarounds.define.util.LogUtils;
import in.workarounds.typography.TextView;

/**
 * Created by madki on 20/09/15.
 */
public class SelectableTextView extends TextView implements SelectMovementMethod.SelectableView{
    private static final String TAG = LogUtils.makeLogTag(SelectableTextView.class);
    List<SelectSpan> total;
    SelectMovementMethod movementMethod;

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

    private void init(){
        movementMethod = new SelectMovementMethod(this);
        if(getText() != null){
            setSelectableText(getText().toString());
        }
        setMovementMethod(movementMethod);
    }

    public void selectText(String needle) throws IllegalArgumentException{
        String text = getText().toString();
        int start = text.indexOf(needle);
        if(start < 0) {
            throw new IllegalArgumentException("'" + needle + "' does not exist in current text in the view : '" + text + "'");
        }

        int end = start + needle.length();
        movementMethod.selectByRange(start, end);
    }

    public void setSelectableText(String text) {
        BreakIterator iterator = BreakIterator.getWordInstance();
        setText(text, TextView.BufferType.SPANNABLE);
        Spannable spans = (Spannable) getText();
        iterator.setText(text);
        total = new ArrayList<>(text.length());
        SelectSpan selectSpan;
        iterator.first();
        int start = 0;
        int index = start;
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                .next()) {
            while(index < end){
                selectSpan = new SelectSpan(index, start, end);
                spans.setSpan(selectSpan, index, index + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                total.add(index, selectSpan);
                index++;
            }
        }
        setText(spans, TextView.BufferType.SPANNABLE);
        movementMethod.setSelectSpanList(total);
    }

    @Override
    public void onSelectionFinished(int selStart, int selEnd) {
        if(mWordSelectedListener != null){
            String word =
                    getText().subSequence(selStart, selEnd).toString();
            mWordSelectedListener.onWordSelected(word);
        }
    }

    public void selectAll(){
        movementMethod.selectAll();
    }

    public void setOnWordSelectedListener(OnWordSelectedListener wordClickListener) {
        this.mWordSelectedListener = wordClickListener;
    }

    public void removeOnWordSelectedListener() {
        this.mWordSelectedListener = null;
    }

    public interface OnWordSelectedListener {
        void onWordSelected(String word);
    }
}
