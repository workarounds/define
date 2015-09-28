package in.workarounds.define.network;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class NetworkModule {
    private static final String TAG = LogUtils.makeLogTag(NetworkModule.class);
    private Context context;

    public NetworkModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        int cacheSize = 10 * 1024 * 1024; //10MB

        Cache cache = null;
        try {
            cache = new Cache(context.getCacheDir(), cacheSize);
        } catch (IOException e) {
            LogUtils.LOGE(TAG, e.getMessage(), e);
        }
        client.setCache(cache);

        // TODO set request interceptors if needed. Adding headers etc

        return client;
    }


}
