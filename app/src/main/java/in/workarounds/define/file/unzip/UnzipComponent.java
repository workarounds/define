package in.workarounds.define.file.unzip;

import dagger.Component;
import dagger.Lazy;
import in.workarounds.define.wordnet.WordnetFileHelper;

/**
 * Created by madki on 27/09/15.
 */
@Component
public interface UnzipComponent {
    Lazy<WordnetFileHelper> wordnetFileHeper();
    void inject(UnzipService unzipService);
}
