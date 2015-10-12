package in.workarounds.define.urban;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.portal.ComponentProvider;
import in.workarounds.define.util.LogUtils;

/**
 * Created by madki on 13/10/15.
 */
public class UrbanMeaningPage extends RelativeLayout {
    private static final String TAG = LogUtils.makeLogTag(UrbanMeaningPage.class);

    @Inject
    UrbanPresenter presenter;

    private TextView title;
    private RecyclerView meanings;

    public UrbanMeaningPage(Context context) {
        super(context);
        init();
    }

    public UrbanMeaningPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UrbanMeaningPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // inflate the view
        inflate(getContext(), R.layout.view_meaning_page, this);

        // inject Presenter
        inject();

        // find the views
        title = (TextView) findViewById(R.id.tv_title);
        meanings = (RecyclerView) findViewById(R.id.rv_meaning_list);

        // set up recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        meanings.setLayoutManager(layoutManager);
        meanings.setAdapter(presenter.adapter());

        // set title
        title(presenter.word());
    }

    private void inject() {
        if(!isInEditMode()) {
            ((ComponentProvider) getContext()).inject(this);
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
