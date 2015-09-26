package in.workarounds.define.portal;

import dagger.Component;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
@Component(modules = {PortalModule.class})
public interface PortalComponent {
    void inject(MainPortal portal);
}
