package in.workarounds.define.portal;

import in.workarounds.define.R;
import in.workarounds.portal.MockActivity;
import in.workarounds.portal.OverlayPermissionHelper;

/**
 * Created by madki on 05/01/16.
 */
public class DefinePermissionHelper extends OverlayPermissionHelper {

    public DefinePermissionHelper(MockActivity mockActivity) {
        super(mockActivity);
    }

    @Override
    protected String getAppName() {
        return context.getString(R.string.app_name);
    }

    @Override
    protected int getAccentColor() {
        return R.color.theme_primary;
    }

    @Override
    protected int getNotificationIcon() {
        return R.drawable.ic_notification_icon;
    }
}
