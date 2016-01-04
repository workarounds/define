package in.workarounds.define.portal;

import android.support.annotation.NonNull;

import in.workarounds.define.service.DefinePortalService;
import in.workarounds.portal.Portal;
import in.workarounds.portal.PortalAdapter;

/**
 * Created by madki on 05/01/16.
 */
public class DefinePortalAdapter extends PortalAdapter<DefinePortalService> {

    public DefinePortalAdapter(DefinePortalService service) {
        super(service);
    }

    @Override
    public int getCount() {
        return PortalId.COUNT;
    }

    @NonNull
    @Override
    protected Portal createPortal(int portalId) {
        switch (portalId) {
            case PortalId.MEANING_PORTAL:
                return new MeaningPortal(getContext(), this);
            case PortalId.UTIL_PORTAL:
                return new UtilPortal(getContext(), this);
            default:
                throw new IllegalStateException("Unknown portalId");
        }
    }
}
