package in.workarounds.define.view.swipeselect;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

/**
 * Created by manidesto on 29/09/15.
 */
public class SelectSpan extends CharacterStyle implements UpdateAppearance{
    private boolean mSelected = false;
    public int index = 0;
    public int start = 0;
    public int end = 0;

    public SelectSpan(int index, int start, int end) {
        this.index = index;
        this.start = start;
        this.end = end;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public boolean isSelected() {
        return mSelected;
    }

    /**
     * Could make the text underlined or change link color.
     */
    @Override
    public void updateDrawState(TextPaint ds) {
        if (mSelected) {
            ds.bgColor = Color.rgb(40, 122, 169);
            ds.setColor(Color.WHITE);
        }
    }
}
