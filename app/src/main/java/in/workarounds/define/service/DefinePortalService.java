package in.workarounds.define.service;

import in.workarounds.define.portal.DefinePermissionHelper;
import in.workarounds.define.portal.DefinePortalAdapter;
import in.workarounds.portal.PortalService;

/**
 * Created by madki on 05/01/16.
 */
public class DefinePortalService extends PortalService<DefinePortalAdapter, DefinePermissionHelper> {

    @Override
    protected DefinePortalAdapter createPortalAdapter() {
        return new DefinePortalAdapter(this);
    }

    @Override
    protected DefinePermissionHelper createPermissionHelper() {
        return new DefinePermissionHelper(this);
    }
}
