package in.workarounds.define.view.meaning;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.portal.ComponentProvider;


/**
 * Created by madki on 26/09/15.
 */
public class MeaningPage extends RelativeLayout {
    @Inject
    MeaningPresenter presenter;

    private TextView title;
    private RecyclerView meanings;

    public MeaningPage(Context context) {
        super(context);
        init();
    }

    public MeaningPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MeaningPage(Context context, AttributeSet attrs, int defStyleAttr) {
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
        presenter.word(word);
        title(word);
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

    public String title() {
        return title.getText().toString();
    }


}