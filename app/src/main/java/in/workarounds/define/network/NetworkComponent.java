package in.workarounds.define.network;

import com.squareup.okhttp.OkHttpClient;

import dagger.Component;

/**
 * Created by madki on 26/09/15.
 */
@Component(modules = NetworkModule.class)
public interface NetworkComponent {
    OkHttpClient client();
}
