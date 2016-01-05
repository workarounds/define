package in.workarounds.define.ui.view.swipeselect;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

import java.util.List;

/**
 * Created by manidesto on 29/09/15.
 */
public class SelectMovementMethod extends LinkMovementMethod{
    SelectableView selectableView;
    List<SelectSpan> total;
    SelectSpan down;
    VelocityTracker velocityTracker;
    PointF touchDown;
    static final float MAX_FINE_MOVEMENT = 20;
    static final float MAX_CLICK_DISTANCE = 24;
    float density;
    boolean userIsSwiping = false;

    public SelectMovementMethod(SelectableView selectableView) {
        this.selectableView = selectableView;
        velocityTracker = VelocityTracker.obtain();
        density = selectableView.getContext().getResources().getDisplayMetrics().density;
    }

    public void setSelectSpanList(List<SelectSpan> spanList) {
        total = spanList;
    }

    public void selectAll(){
        selectByRange(0, total.size());
    }

    public void selectByRange(int start, int end) throws IllegalArgumentException{
        if(!isValidRange(start, end)) throw new IllegalArgumentException("(" + start + ", " + end + ") is not a valid range");
        highlightByRange(start, end);
        selectableView.invalidate();
        selectableView.onSelectionFinished(start, end);
    }

    @Override
    public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer,
                                @NonNull MotionEvent event) {
        int action = event.getAction();
        velocityTracker.addMovement(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        SelectSpan[] links = buffer.getSpans(off, off, SelectSpan.class);

        if (links.length != 0) {
            SelectSpan current = links[0];
            velocityTracker.computeCurrentVelocity(1000);
            float vel = velocityTracker.getXVelocity();
            if (vel < 0) {
                vel = -vel;
            }
            boolean approximate = vel > MAX_FINE_MOVEMENT * density;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    for (SelectSpan span : total) {
                        span.setSelected(false);
                    }
                    userIsSwiping = false;
                    setTouchDown(event);
                    down = current;
                    down.setSelected(true);
                    break;
                case MotionEvent.ACTION_UP:
                    if (down == null) {
                        return false;
                    }

                    if (!userIsSwiping) {
                        selectWord(current);
                    } else {
                        selectSpansFromDown(current, approximate, true);
                    }
                    velocityTracker.clear();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (down == null) {
                        return false;
                    }

                    if (!userIsSwiping) {
                        userIsSwiping = !isCloseToTouchDown(event);
                    }
                    selectSpansFromDown(current, approximate);
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_CANCEL:
                    down = null;
                    touchDown = null;
                    userIsSwiping = false;
                    velocityTracker.clear();
                    return false;
                default:
                    break;
            }
            selectableView.invalidate();
            return true;
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    private void selectSpansFromDown(SelectSpan current, boolean approximate){
        selectSpansFromDown(current, approximate, false);
    }

    private void selectSpansFromDown(SelectSpan current, boolean approximate, boolean notify) {
        int start = down.index;
        int end = current.index;
        if (approximate) {
            start = getApproximateEdge(down);
            if (start != down.index && start < total.size()) {
                down = total.get(start);
            }
            end = getApproximateEdge(current);
        }
        if (end < start) {
            int temp = end;
            end = start;
            start = temp;
        }

        highlightByRange(start, end);

        if(notify) {
            selectableView.onSelectionFinished(start, end);
        }
    }

    private void selectWord(SelectSpan word) {
        highlightByRange(word.start, word.end);

        selectableView.onSelectionFinished(word.start, word.end);
    }

    private void highlightByRange(int start, int end){
        for (SelectSpan span : total) {
            boolean selected = span.index >= start
                    && span.index < end;
            span.setSelected(selected);
        }
    }

    private int getApproximateEdge(SelectSpan span) {
        int d2start = span.index - span.start;
        int d2end = span.end - span.index;
        if (d2start < d2end && d2start <= 2) {
            return span.start;
        } else if (d2end < d2start && d2end <= 2) {
            return span.end;
        } else {
            return span.index;
        }
    }

    private void setTouchDown(MotionEvent event) {
        if (touchDown == null) {
            touchDown = new PointF(0, 0);
        }
        touchDown.x = event.getX();
        touchDown.y = event.getY();
    }

    private boolean isCloseToTouchDown(MotionEvent event) {
        float dx = event.getX() - touchDown.x;
        float dy = event.getY() - touchDown.y;
        double dr = Math.sqrt(dx * dx + dy * dy);
        return dr <= MAX_CLICK_DISTANCE * density;
    }

    private boolean isValidRange(int start, int end) {
        int size = total.size();
        //start is inclusive, end is exclusive
        return start >= 0 && end <= size;
    }

    public interface SelectableView {
        Context getContext();
        void onSelectionFinished(int start, int end);
        void invalidate();
    }
}
