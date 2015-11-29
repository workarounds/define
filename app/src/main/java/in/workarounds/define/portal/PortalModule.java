package in.workarounds.define.portal;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.base.NotificationUtils;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class PortalModule {
    private MainPortal portal;

    public PortalModule(MainPortal portal) {
        this.portal = portal;
    }

    @Provides @PerPortal
    public NotificationUtils provideNotificationUtils() {
        return new NotificationUtils(portal);
    }

    @Provides @PerPortal
    public PortalView providesPortalView() {
        return portal;
    }

    @Provides @PerPortal
    public PortalPresenter providesPortalPresenter(PortalView portalView, NotificationUtils notificationUtils) {
        return new MainPortalPresenter(portalView, notificationUtils);
    }
}