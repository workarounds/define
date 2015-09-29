package in.workarounds.define.ui.view;

import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import in.workarounds.define.util.LogUtils;
import in.workarounds.typography.EditText;

/**
 * Created by madki on 20/09/15.
 */
public class SelectableTextView extends EditText implements View.OnTouchListener{
    private static final String TAG = LogUtils.makeLogTag(SelectableTextView.class);

    //Maximum allowed distance between touch down point and touch up point
    private static final int MAX_CLICK_DISTANCE = 48;

    private OnWordSelectedListener mWordSelectedListener;
    private PointF mTouchDown;

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
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }
        setTextIsSelectable(true);
        setOnTouchListener(this);
//        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onTouch(View v, @NonNull final MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mTouchDown = getTouchPoint(event);
                break;
            case MotionEvent.ACTION_UP:
                if(mTouchDown == null){
                    break;
                }

                final PointF up = getTouchPoint(event);
                if(isClick(mTouchDown, up)){
                    //send down to super
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            event.setAction(MotionEvent.ACTION_DOWN);
                            event.setLocation(up.x, up.y);
                            SelectableTextView.this.onTouchEvent(event);
                        }
                    }, 100);
                    //send up to super
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            event.setAction(MotionEvent.ACTION_UP);
                            event.setLocation(up.x, up.y);
                            SelectableTextView.this.onTouchEvent(event);
                        }
                    }, 200);
                }

                mTouchDown = null;
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                mTouchDown = null;
                break;
            default:
                break;
        }
        return this.onTouchEvent(event);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if(mWordSelectedListener != null){
            String word =
                    getText().subSequence(selStart, selEnd).toString();
            mWordSelectedListener.onWordSelected(word);
        }
    }

    public void setOnWordSelectedListener(OnWordSelectedListener wordClickListener) {
        this.mWordSelectedListener = wordClickListener;
    }

    public void removeOnWordSelectedListener() {
        this.mWordSelectedListener = null;
    }

    /**
     * Checks whether the down and up events are close enough to be detected
     * as a click
     * @param down Touch down MotionEvent
     * @param up Touch up MotionEvent
     * @return true if up and down are close enough, false otherwise
     */
    private boolean isClick(PointF down, PointF up){
        float density = getContext().getResources().getDisplayMetrics().density;
        return areWithinDistance(down, up, MAX_CLICK_DISTANCE *density);
    }

    private static boolean areWithinDistance(PointF a, PointF b, double distance){
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        double dr = Math.sqrt(dx*dx + dy*dy);
        return dr <= distance;
    }

    private static PointF getTouchPoint(MotionEvent e){
        return new PointF(e.getX(), e.getY());
    }

    public interface OnWordSelectedListener {
        void onWordSelected(String word);
    }
}
