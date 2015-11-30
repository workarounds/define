package in.workarounds.define.portal;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.view.SelectionCard.SelectionCardController;

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
    public MeaningsController providesMeaningsController() {
        return portal;
    }

    @Provides @PerPortal
    public SelectionCardController providesSelectionCardListener() {
        return portal;
    }
}