package in.workarounds.define.webviewDicts;

import dagger.Component;
import in.workarounds.define.portal.PerPortal;

/**
 * Created by Nithin on 13/10/15.
 */
@PerPortal
@Component(modules = {WebViewModule.class})
public interface WebViewComponent {
    JavaScriptInterface provideJavaScriptInterface();
}
