package in.workarounds.define.portal;

import android.support.annotation.NonNull;

import in.workarounds.define.service.DefinePortalService;
import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalAdapter;

import static in.workarounds.define.portal.PortalId.MEANING_PORTAL;
import static in.workarounds.define.portal.PortalId.UTIL_PORTAL;

/**
 * Created by madki on 05/01/16.
 */
public class DefinePortalAdapter extends PortalAdapter<DefinePortalService> {

    public DefinePortalAdapter(DefinePortalService service, int themeId) {
        super(service, themeId);
    }

    @Override
    public int getCount() {
        return PortalId.COUNT;
    }

    @NonNull
    @Override
    protected Portal createPortal(int portalId) {
        switch (portalId) {
            case MEANING_PORTAL:
                return new MeaningPortal(this, MEANING_PORTAL);
            case UTIL_PORTAL:
                return new UtilPortal(this, UTIL_PORTAL);
            default:
                throw new IllegalStateException("Unknown portalId");
        }
    }

    public void startForeground() {
        service.startForeground();
    }
}
