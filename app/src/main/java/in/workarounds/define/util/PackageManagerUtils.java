package in.workarounds.define.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by Nithin on 13/10/15.
 */
public class PackageManagerUtils {

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
