package in.workarounds.define.api;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.urban.UrbanDictionaryApi;
import retrofit.RestAdapter;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class ApiModule {

    @Provides @PerPortal
    public UrbanDictionaryApi provideUrbanApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.urbandictionary.com/v0")
                .build();

        return restAdapter.create(UrbanDictionaryApi.class);
    }
}
