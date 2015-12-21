package in.workarounds.define.log;

import android.util.Log;

import timber.log.Timber;

/**
 * Created by manidesto on 01/12/15.
 */
public class ReleaseTree extends Timber.Tree{

    @Override
    protected boolean isLoggable(int priority) {
        if(priority == Log.DEBUG || priority == Log.VERBOSE || priority == Log.INFO) {
            return false;
        }
        //Only WARN, ERROR, WTF
        return true;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if(isLoggable(priority)) {
            Log.println(priority, tag, message);
        }
    }
}
