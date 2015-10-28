package in.workarounds.define.webviewDicts.livio;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.DefineApp;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class LivioModule {
    @Provides
    public Context provideContext() {
        return DefineApp.getContext();
    }
}
