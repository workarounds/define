package in.workarounds.define.log;

import timber.log.Timber;

/**
 * Created by manidesto on 01/12/15.
 */
public class DebugTree extends Timber.DebugTree{
    //Adds line number to logs
    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return super.createStackElementTag(element) + ":" + element.getLineNumber();
    }
}
