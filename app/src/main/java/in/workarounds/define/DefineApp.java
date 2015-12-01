package in.workarounds.define;

import android.app.Application;
import android.content.Context;

import in.workarounds.define.log.DebugTree;
import in.workarounds.define.log.ReleaseTree;
import timber.log.Timber;

/**
 * Created by Nithin on 27/10/15.
 */
public class DefineApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}
