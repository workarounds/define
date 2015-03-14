package in.workarounds.define.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

	private int line_height;

	public static class LayoutParams extends ViewGroup.LayoutParams {

		public final int horizontal_spacing;
		public final int vertical_spacing;
		public final boolean nextLine;

		/**
		 * @param horizontal_spacing
		 *            Pixels between items, horizontally
		 * @param vertical_spacing
		 *            Pixels between items, vertically
		 * @param nextLine
		 *            should it add a line now?
		 */
		public LayoutParams(int horizontal_spacing, int vertical_spacing,
				boolean nextLine) {
			super(0, 0);
			this.horizontal_spacing = horizontal_spacing;
			this.vertical_spacing = vertical_spacing;
			this.nextLine = nextLine;
		}
	}

	public FlowLayout(Context context) {
		super(context);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!(MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED))
			return;

		final int width = MeasureSpec.getSize(widthMeasureSpec)
				- getPaddingLeft() - getPaddingRight();
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()
				- getPaddingBottom();
		final int count = getChildCount();
		int line_height = 0;

		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();

		int childHeightMeasureSpec;
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
					MeasureSpec.AT_MOST);
		} else {
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				child.measure(
						MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
						childHeightMeasureSpec);
				final int childw = child.getMeasuredWidth();
				line_height = Math.max(line_height, child.getMeasuredHeight()
						+ lp.vertical_spacing);

				if ((xpos + childw + getPaddingRight() + lp.horizontal_spacing > width)
						|| lp.nextLine) {
					xpos = getPaddingLeft();
					ypos += line_height;
					if (lp.nextLine)
						ypos += line_height;
				}

				xpos += childw + lp.horizontal_spacing;
			}
		}
		this.line_height = line_height;

		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			height = ypos + line_height;

		} else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			if (ypos + line_height < height) {
				height = ypos + line_height;
			}
		}
		setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(),
				height + getPaddingBottom());
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(1, 1, false); // default of 1px spacing
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		if (p instanceof LayoutParams) {
			return true;
		}
		return false;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		final int width = r - l;
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final int childw = child.getMeasuredWidth();
				final int childh = child.getMeasuredHeight();
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if ((xpos + childw + getPaddingRight() + lp.horizontal_spacing > width)
						|| lp.nextLine) {
					xpos = getPaddingLeft();
					ypos += line_height;
					if (lp.nextLine)
						ypos += line_height;
				}
				child.layout(xpos, ypos, xpos + childw, ypos + childh);
				xpos += childw + lp.horizontal_spacing;
			}
		}
	}
}