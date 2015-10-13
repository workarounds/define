package in.workarounds.define.portal;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class PortalModule {
    private MainPortal portal;

    public PortalModule(MainPortal portal) {
        this.portal = portal;
    }

    @Provides
    public MainPortal provideMainPortal() {
        return portal;
    }

    @Provides
    public Context provideContext() {
        return portal.getApplicationContext();
    }
}

