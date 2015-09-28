package in.workarounds.define.urban;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.dictionary.Dictionary;
import in.workarounds.define.portal.PerPortal;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.urbandictionary.com/v0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(UrbanApi.class);
    }
}
