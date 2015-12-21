package in.workarounds.define.webviewDicts.livio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.workarounds.define.R;
import in.workarounds.define.constants.DictionaryConstants;
import in.workarounds.define.portal.ComponentProvider;
import in.workarounds.define.portal.PortalComponent;
import in.workarounds.define.webviewDicts.livio.presenter.LivioBasePresenter;

/**
 * Created by madki on 13/10/15.
 */
public class LivioMeaningPage extends RelativeLayout {
    private static final int ERROR = 1;
    private static final int LOADING = 2;
    private static final int MEANING_LIST = 3;

    private static final String mime = "text/html";
    private static final String encoding = "utf-8";

    LivioBasePresenter presenter;

    private TextView title;
    private WebView meanings;
    private TextView error;
    private Button installLivioBtn;
    private ProgressBar loadingIndicator;
    private int dictId;


    public LivioMeaningPage(Context context, int dictId) {
        super(context);
        this.dictId = dictId;
        init();
    }

    public LivioMeaningPage(Context context) {
        super(context);
        init();
    }

    public LivioMeaningPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LivioMeaningPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /* Ignore security vulnerability warnings for sdk below below jelly bean as we are in control of the html */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init() {
        // inflate the view
        inflate(getContext(), R.layout.livio_view_meaning_page, this);

        // inject Presenter
        inject();

        // find the views
        findTheViews();

        installLivioBtn.setOnClickListener(v -> presenter.onInstallClicked());

        meanings.getSettings().setJavaScriptEnabled(true);
        meanings.addJavascriptInterface(this, "JSInterface");
        meanings.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                meaningsLoaded();
            }
        });
    }

    private void findTheViews() {
        title = (TextView) findViewById(R.id.tv_title);
        meanings = (WebView) findViewById(R.id.rv_meaning_list);
        error = (TextView) findViewById(R.id.tv_load_status);
        installLivioBtn = (Button) findViewById(R.id.btn_install_livio);
        loadingIndicator = (ProgressBar) findViewById(R.id.pb_load_progress);
    }

    private void inject() {
        if (!isInEditMode()) {
            PortalComponent component = ((ComponentProvider) getContext()).component();
            switch (dictId) {
                case DictionaryConstants.LIVIO_EN:
                    presenter = component.livioEnglishPresenter();
                    break;
                case DictionaryConstants.LIVIO_FR:
                    presenter = component.livioFrenchPresenter();
                    break;
                case DictionaryConstants.LIVIO_IT:
                    presenter = component.livioItalianPresenter();
                    break;
                case DictionaryConstants.LIVIO_ES:
                    presenter = component.livioSpanishPresenter();
                    break;
                case DictionaryConstants.LIVIO_DE:
                    presenter = component.livioGermanPresenter();
                    break;
                default:
                    throw new IllegalStateException("Unknown Dictionary id provided to LivioMeaningPage");
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            presenter.addView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            presenter.dropView();
        }
    }

    public void title(String heading) {
        title.setText(heading);
    }

    public void error(String msg) {
        error.setText(msg);
        showView(ERROR);
    }

    public void updateMeanings(String html) {
        post(() -> {
            if(meanings != null) {
                meanings.loadDataWithBaseURL("file:///android_asset/", html, mime, encoding, null);
            }
        });
    }

    @android.webkit.JavascriptInterface
    public void loadMeaning(final String word) {
        post(() -> {
            // code here will run on UI thread
            presenter.onWordUpdated(word.split(":")[1]);
        });
    }

    public void meaningsLoading() {
        showView(LOADING);
    }

    public void meaningsLoaded() {
        showView(MEANING_LIST);
    }

    public void dictionaryNotAvailable() {
        installLivioBtn.setVisibility(View.VISIBLE);
    }

    private void showView(@ViewEnum int view) {
        switch (view) {
            case ERROR:
                changeViewVisibilities(true, false, false);
                break;
            case LOADING:
                changeViewVisibilities(false, true, false);
                break;
            case MEANING_LIST:
                changeViewVisibilities(false, false, true);
                break;
        }
    }

    private void changeViewVisibilities(boolean status, boolean progress, boolean list) {
        error.setVisibility(status ? View.VISIBLE : View.GONE);

        loadingIndicator.setVisibility(progress ? View.VISIBLE : View.GONE);

        meanings.setVisibility(list ? View.VISIBLE : View.GONE);

        if(list) {
            installLivioBtn.setVisibility(View.GONE);
        }
    }

    @IntDef({LOADING, ERROR, MEANING_LIST})
    private @interface ViewEnum {
    }
}
