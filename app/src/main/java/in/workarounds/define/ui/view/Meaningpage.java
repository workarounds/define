package in.workarounds.define.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.portal.ComponentProvider;

/**
 * Created by madki on 26/09/15.
 */
public class MeaningPage extends RelativeLayout {
    @Inject
    MeaningPresenter presenter;

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


}
