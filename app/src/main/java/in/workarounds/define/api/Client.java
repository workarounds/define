package in.workarounds.define.api;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import in.workarounds.define.util.LogUtils;

public class Client {

    private static final String TAG = LogUtils.makeLogTag(Client.class);

    private static OkHttpClient mClient;

    private Client() {
        mClient = new OkHttpClient();
    }

    public static OkHttpClient getClientInstance(Context context) {
        if (mClient == null) {
            mClient = new OkHttpClient();
            setCache(context);
        }
        return mClient;
    }

    public static Response call(Request request) throws IOException {
        if (mClient == null) {
            mClient = new OkHttpClient();

        }
        return mClient.newCall(request).execute();
    }

    public static void setCache(Context context) {
        //TODO change cacheSize according to requirement
        int cacheSize = 10 * 1024 * 1024; //10MB

        Cache cache = null;
        try {
            cache = new Cache(context.getCacheDir(), cacheSize);
        } catch (IOException e) {
            LogUtils.LOGE(TAG, e.getMessage(), e);
        }
        mClient.setCache(cache);
    }
}
