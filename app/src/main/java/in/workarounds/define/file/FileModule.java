package in.workarounds.define.file;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.wordnet.Wordnet;
import in.workarounds.define.wordnet.WordnetFileHelper;

/**
 * Created by madki on 26/09/15.
 */
@Module
public class FileModule {

    @Provides @Wordnet
    public FileHelper wordnet() {
        return new WordnetFileHelper();
    }

}
