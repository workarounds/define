package in.workarounds.define.portal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import in.workarounds.define.R;
import in.workarounds.define.base.MeaningPagerAdapter;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.network.DaggerNetworkComponent;
import in.workarounds.define.network.NetworkModule;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.view.slidingtabs.SlidingTabLayout;
import in.workarounds.define.view.swipeselect.SelectableTextView;
import in.workarounds.portal.Portal;

/**
 * Created by madki on 20/09/15.
 */
public class MainPortal extends Portal implements ComponentProvider {
    private static final String TAG = LogUtils.makeLogTag(MainPortal.class);
    public static final String BUNDLE_KEY_CLIP_TEXT = "bundle_key_clip_text";
    private String mClipText;

    private SelectableTextView mTvClipText;
    private ViewPager pager;

    private View mPortalContainer;
    private View meaningPagesContainer;
    private PortalComponent component;

    private List<MeaningPresenter> presenters = new ArrayList<>();

    public MainPortal(Context base) {
        super(base);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initComponents();
        setContentView(R.layout.portal_main);
        initViews();
        extractClipText(bundle);
        setClipTextToCard();
    }

    private void initComponents() {
        component = DaggerPortalComponent.builder()
                .portalModule(new PortalModule(this))
                .networkComponent(
                        DaggerNetworkComponent.builder()
                                .networkModule(new NetworkModule(this)).build())
                .build();
    }

    @Override
    public PortalComponent component() {
        return component;
    }

    private void initViews() {
        mPortalContainer = findViewById(R.id.rl_main_portal_container);
        mTvClipText = (SelectableTextView) findViewById(R.id.tv_clip_text);
        meaningPagesContainer = findViewById(R.id.ll_meaning_pages_container);
        meaningPagesContainer.setVisibility(View.INVISIBLE);

        mPortalContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pager = (ViewPager) findViewById(R.id.vp_pages);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(new MeaningPagerAdapter());
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        if(slidingTabLayout != null) {
            slidingTabLayout.setDistributeEvenly(true);
            slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.theme_primary));
            slidingTabLayout.setCustomTabView(R.layout.layout_sliding_tabs, R.id.tv_tab_header);
            slidingTabLayout.setViewPager(pager);
        }

        mTvClipText.setOnWordSelectedListener(new SelectableTextView.OnWordSelectedListener() {
            @Override
            public void onWordSelected(String word) {
                meaningPagesContainer.setVisibility(View.VISIBLE);
                for(MeaningPresenter presenter: presenters) {
                    presenter.onWordUpdated(word);
                }
            }
        });
    }

    private void extractClipText(Bundle bundle) {
        if(bundle != null && bundle.containsKey(BUNDLE_KEY_CLIP_TEXT)) {
            LogUtils.LOGD(TAG, "bundle clip text: " + bundle.getString(BUNDLE_KEY_CLIP_TEXT));
            mClipText = bundle.getString(BUNDLE_KEY_CLIP_TEXT);
        }
    }

    private void setClipTextToCard() {
        if(mClipText != null) {
            mTvClipText.setSelectableText(mClipText);
        }
    }

    public void addPresenter(MeaningPresenter presenter) {
        if(!presenters.contains(presenter)) {
            presenters.add(presenter);
        } else {
            LogUtils.LOGE(TAG, "Presented already present. Not adding");
        }
    }

    public void removePresenter(MeaningPresenter presenter) {
        if(presenters.contains(presenter)) {
            presenters.remove(presenter);
        } else {
            LogUtils.LOGE(TAG, "Presenter isn't present. Not removing");
        }
    }

}
