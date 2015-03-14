package in.workarounds.define.util;


import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {

    /**
     * Network status constants
     */
    public static final int WIFI = 1;
    public static final int MOBILE = 2;
    public static final int NO_NETWORK = 3;

    /**
     * check network status
     * @param context context
     * @return status of network as int
     */
    public static int checkNetworkStatus(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isAvailable()) {
            return WIFI;
        } else if (mobile.isAvailable()) {
            return MOBILE;
        } else {
            return NO_NETWORK;
        }
    }
}
