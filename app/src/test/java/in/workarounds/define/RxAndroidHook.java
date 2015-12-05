package in.workarounds.define;

import rx.Scheduler;
import rx.android.plugins.RxAndroidSchedulersHook;

/**
 * Created by manidesto on 05/12/15.
 */
public class RxAndroidHook extends RxAndroidSchedulersHook {
    Scheduler scheduler;

    public RxAndroidHook(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Scheduler getMainThreadScheduler() {
        return scheduler;
    }
}
