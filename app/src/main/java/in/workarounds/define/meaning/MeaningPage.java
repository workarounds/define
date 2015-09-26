package in.workarounds.define.meaning;

import android.content.Context;
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

    public MeaningPage(Context context) {
        super(context);
        init();
        inject();
    }

    public MeaningPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        inject();
    }

    public MeaningPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        inject();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_meaning_page, this);
        title = (TextView) findViewById(R.id.tv_title);
    }

    private void inject() {
        if(!isInEditMode()) {
            ((ComponentProvider) getContext()).inject(this);
        }
    }

    public void setWord(String word) {
        presenter.word(word);
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
