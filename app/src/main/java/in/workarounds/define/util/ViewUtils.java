package in.workarounds.define.util;

import android.widget.ProgressBar;

/**
 * Created by manidesto on 30/09/15.
 */
public class ViewUtils {
    public static void setColorOfProgressBar(ProgressBar progressBar, int color){
        progressBar.getIndeterminateDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
    }
}
