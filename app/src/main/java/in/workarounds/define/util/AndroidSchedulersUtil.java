package in.workarounds.define.util;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by manidesto on 05/12/15.
 * A shim around {@link rx.android.schedulers.AndroidSchedulers} until the
 * fix for issue [#238](https://github.com/ReactiveX/RxAndroid/issues/238),
 * the Lazy static initializer for main thread scheduler is released.
 *
 * Issue #238 makes the usage of AndroidSchedulers.mainThread() untestable
 */
public class AndroidSchedulersUtil {
    public static Scheduler mainThread() {
        Scheduler scheduler = RxAndroidPlugins.getInstance()
                .getSchedulersHook().getMainThreadScheduler();
        return scheduler != null ? scheduler : AndroidSchedulers.mainThread();
    }
}
