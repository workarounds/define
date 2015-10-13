package in.workarounds.define.webviewDicts.livio;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.portal.ComponentProvider;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 13/10/15.
 */
public class LivioMeaningPage extends RelativeLayout {
    private static final String TAG = LogUtils.makeLogTag(LivioMeaningPage.class);

    @Inject
    LivioPresenter presenter;

    private TextView title;
    private WebView meanings;

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
        if(!isInEditMode()) {
            ((ComponentProvider) getContext()).component().inject(this);
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
        if(!isInEditMode()) {
            presenter.addView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.LOGD(TAG, "Detached from window");
        if(!isInEditMode()) {
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
