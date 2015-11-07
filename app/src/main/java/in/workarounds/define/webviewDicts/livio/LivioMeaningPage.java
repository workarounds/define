package in.workarounds.define.webviewDicts.livio;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.workarounds.define.R;
import in.workarounds.define.constants.DictionaryId;
import in.workarounds.define.portal.ComponentProvider;
import in.workarounds.define.portal.PortalComponent;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.webviewDicts.livio.presenter.LivioBasePresenter;

/**
 * Created by madki on 13/10/15.
 */
public class LivioMeaningPage extends RelativeLayout {
    private static final String TAG = LogUtils.makeLogTag(LivioMeaningPage.class);

    LivioBasePresenter presenter;

    private TextView title;
    private WebView meanings;
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

    private void init() {
        // inflate the view
        inflate(getContext(), R.layout.livio_view_meaning_page, this);

        // inject Presenter
        inject();

        // find the views
        title = (TextView) findViewById(R.id.tv_title);
        meanings = (WebView) findViewById(R.id.rv_meaning_list);

        // set title
        title(presenter.word());
    }

    private void inject() {
        if (!isInEditMode()) {
            PortalComponent component = ((ComponentProvider) getContext()).component();
            switch (dictId) {
                case DictionaryId.LIVIO_EN:
                    presenter = component.livioEnglishPresenter();
                    break;
                case DictionaryId.LIVIO_FR:
                    presenter = component.livioFrenchPresenter();
                    break;
                case DictionaryId.LIVIO_IT:
                    presenter = component.livioItalianPresenter();
                    break;
                case DictionaryId.LIVIO_ES:
                    presenter = component.livioSpanishPresenter();
                    break;
                case DictionaryId.LIVIO_DE:
                    presenter = component.livioGermanPresenter();
                    break;
                default:
                    throw new IllegalStateException("Unknown Dictionary id provided to LivioMeaningPage");
            }
        }
    }

    public void setWord(String word) {
        presenter.onWordUpdated(word);
        title(word);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtils.LOGD(TAG, "Attached to window");
        if (!isInEditMode()) {
            presenter.addView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.LOGD(TAG, "Detached from window");
        if (!isInEditMode()) {
            presenter.dropView();
        }
    }

    public void title(String heading) {
        title.setText(heading);
    }

    public String title() {
        return title.getText().toString();
    }

}
