package in.workarounds.define.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import in.workarounds.define.R;

/**
 * Created by madki on 01/10/15.
 */
public class MaxHeightScrollView extends ScrollView {
    private float maxHeight;

    public MaxHeightScrollView(Context context) {
        super(context);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray scrollViewAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView);
        try {
            maxHeight = scrollViewAttrs.getDimension(R.styleable.MaxHeightScrollView_maxHeight, 0);
        } finally {
            scrollViewAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(maxHeight != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(maxHeight), MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
