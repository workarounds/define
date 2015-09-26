package in.workarounds.define.urban;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.portal.PerPortal;
import retrofit.RestAdapter;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class UrbanModule {

    @Provides @PerPortal
    public Dictionary provideDictionary(UrbanDictionary dictionary) {
        return dictionary;
    }

    @Provides @PerPortal
    public UrbanApi provideUrbanApi() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.urbandictionary.com/v0")
                .build();

        return restAdapter.create(UrbanApi.class);
    }
}
