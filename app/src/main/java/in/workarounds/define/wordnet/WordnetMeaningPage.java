package in.workarounds.define.wordnet;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import in.workarounds.define.R;
import in.workarounds.define.portal.ComponentProvider;


/**
 * Created by madki on 26/09/15.
 */
public class WordnetMeaningPage extends RelativeLayout {
    private static final int ERROR = 1;
    private static final int LOADING = 2;
    private static final int MEANING_LIST = 3;

    @Inject
    WordnetPresenter presenter;
    @Inject
    WordnetMeaningAdapter adapter;

    private TextView title;
    private RecyclerView meanings;

    private TextView error;
    private View downloadButton;
    private View loadingIndicator;

    public WordnetMeaningPage(Context context) {
        super(context);
        init();
    }

    public WordnetMeaningPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WordnetMeaningPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // inflate the view
        inflate(getContext(), R.layout.view_meaning_page, this);

        // inject Presenter
        inject();

        // find the views
        findTheViews();

        downloadButton.setOnClickListener(v -> presenter.onDownloadClicked());

        // set up recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        meanings.setLayoutManager(layoutManager);
        meanings.setAdapter(adapter);
    }

    private void inject() {
        if(!isInEditMode()) {
            ((ComponentProvider) getContext()).component().inject(this);
        }
    }

    private void findTheViews() {
        title = (TextView) findViewById(R.id.tv_title);
        meanings = (RecyclerView) findViewById(R.id.rv_meaning_list);
        error = (TextView) findViewById(R.id.tv_load_status);
        downloadButton = findViewById(R.id.btn_download_wordnet);
        loadingIndicator = findViewById(R.id.pb_load_progress);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!isInEditMode()) {
            presenter.addView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!isInEditMode()) {
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

    public void meanings(List<Synset> meanings) {
        adapter.update(meanings);
        showView(MEANING_LIST);
    }

    public void meaningsLoading() {
        showView(LOADING);
    }

    public void dictionaryNotAvailable() {
        downloadButton.setVisibility(View.VISIBLE);
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
            downloadButton.setVisibility(View.GONE);
        }
    }

    @IntDef({LOADING, ERROR, MEANING_LIST})
    private @interface ViewEnum {
    }
}
