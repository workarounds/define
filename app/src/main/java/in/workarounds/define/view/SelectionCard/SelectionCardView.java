package in.workarounds.define.view.SelectionCard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.portal.ComponentProvider;
import in.workarounds.define.view.swipeselect.SelectableTextView;

/**
 * Created by manidesto on 30/11/15.
 */
public class SelectionCardView extends FrameLayout implements View.OnClickListener{
    @Inject SelectionCardPresenter presenter;

    SelectableTextView selectableTextView;

    public SelectionCardView(Context context) {
        super(context);
        init();
    }

    public SelectionCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectionCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
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
            presenter.removeView();
        }
    }

    public void setTextForSelection(String text) {
        selectableTextView.setSelectableText(text);
    }

    public void selectAll() {
        selectableTextView.selectAll();
    }

    private void init() {
        //inflate the view
        inflate(getContext(), R.layout.view_selection_card, this);

        //inject presenter
        inject();

        //retrieve the views
        selectableTextView = (SelectableTextView) findViewById(R.id.tv_clip_text);
        selectableTextView.setOnWordSelectedListener(presenter);
        initButtons();
    }

    private void inject() {
        if(!isInEditMode()) {
            ((ComponentProvider) getContext()).component().inject(this);
        }
    }

    private void initButtons() {
        findViewById(R.id.button_define).setOnClickListener(this);
        findViewById(R.id.button_search).setOnClickListener(this);
        findViewById(R.id.button_copy).setOnClickListener(this);
        findViewById(R.id.button_wiki).setOnClickListener(this);
        findViewById(R.id.button_share).setOnClickListener(this);
        findViewById(R.id.button_settings).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_define:
                presenter.onDefineClicked();
                break;
            case R.id.button_search:
                presenter.onSearchClicked();
                break;
            case R.id.button_copy:
                presenter.onCopyClicked();
                break;
            case R.id.button_wiki:
                presenter.onWikiClicked();
                break;
            case R.id.button_share:
                presenter.onShareClicked();
                break;
            case R.id.button_settings:
                presenter.onSettingsClicked();
                break;
            default:
                break;
        }
    }
}
