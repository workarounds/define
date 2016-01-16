package in.workarounds.define.service;

import android.support.annotation.NonNull;

import in.workarounds.define.R;
import in.workarounds.define.portal.DefinePermissionHelper;
import in.workarounds.define.portal.DefinePortalAdapter;
import in.workarounds.portal.PortalService;

/**
 * Created by madki on 05/01/16.
 */
public class DefinePortalService extends PortalService<DefinePortalAdapter, DefinePermissionHelper> {

    @NonNull
    @Override
    protected DefinePortalAdapter createPortalAdapter() {
        return new DefinePortalAdapter(this, R.style.AppTheme);
    }

    @Override
    protected DefinePermissionHelper createPermissionHelper() {
        return new DefinePermissionHelper(this);
    }
}
