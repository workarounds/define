package in.workarounds.define;

import android.app.Application;
import android.content.Context;

/**
 * Created by Nithin on 27/10/15.
 */
public class DefineApp extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}
