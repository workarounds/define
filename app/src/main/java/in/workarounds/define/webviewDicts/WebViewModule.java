package in.workarounds.define.webviewDicts;

import android.os.Handler;

import dagger.Module;
import dagger.Provides;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.portal.PerPortal;

/**
 * Created by Nithin on 13/10/15.
 */

@Module
public class WebViewModule {

    private MeaningPresenter presenter;
    private Handler handler;

    public WebViewModule(MeaningPresenter presenter, Handler handler){
        this.presenter = presenter;
        this.handler = handler;
    }

    @Provides
    @PerPortal
    public JavaScriptInterface provideJavaScriptInterface() {
        return new JavaScriptInterface(presenter,handler);
    }
}
