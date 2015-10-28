package in.workarounds.define.portal;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

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
public class MainPortal extends Portal implements ComponentProvider, View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(MainPortal.class);
    public static final String BUNDLE_KEY_CLIP_TEXT = "bundle_key_clip_text";
    private String mClipText;

    private SelectableTextView mTvClipText;
    private ViewPager pager;

    private String selectedText;

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

    @NonNull
    @Override
    protected WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams params = super.getLayoutParams();
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        return params;
    }

    private void initViews() {
        mPortalContainer = findViewById(R.id.rl_main_portal_container);
        mTvClipText = (SelectableTextView) findViewById(R.id.tv_clip_text);
        meaningPagesContainer = findViewById(R.id.ll_meaning_pages_container);
//        if (meaningPagesContainer != null) {
//            meaningPagesContainer.setVisibility(View.INVISIBLE);
//        }

        mPortalContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pager = (ViewPager) findViewById(R.id.vp_pages);
        pager.setAdapter(new MeaningPagerAdapter(this));
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        if(slidingTabLayout != null) {
            slidingTabLayout.setDistributeEvenly(true);
            slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.theme_primary));
            slidingTabLayout.setCustomTabView(R.layout.layout_sliding_tabs, R.id.tv_tab_header);
            slidingTabLayout.setViewPager(pager);
        }

        mTvClipText.setOnWordSelectedListener(new SelectableTextView.OnWordSelectedListener() {
            @Override
            public void onWordSelected(String word) {
                selectedText = word;
                meaningPagesContainer.setVisibility(View.VISIBLE);
                for (MeaningPresenter presenter : presenters) {
                    presenter.onWordUpdated(word);
                }
            }
        });
        initButtons();
        meaningPagesContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                meaningPagesContainer.setVisibility(View.INVISIBLE);
            }
        }, 350);
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
            presenter.onWordUpdated(selectedText);
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

    private void initButtons() {
        findViewById(R.id.button_search).setOnClickListener(this);
        findViewById(R.id.button_copy).setOnClickListener(this);
        findViewById(R.id.button_wiki).setOnClickListener(this);
    }

    private void copySelectedText() {
        if(TextUtils.isEmpty(selectedText)) return;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Define", selectedText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
    }

    private void searchSelectedText() {
        if(TextUtils.isEmpty(selectedText)) return;

        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, selectedText);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void wikiSelectedText() {
        if(TextUtils.isEmpty(selectedText)) return;
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://en.m.wikipedia.org/wiki/" + selectedText));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onClick(View v) {
       int id = v.getId();
        switch (id) {
            case R.id.button_search:
                searchSelectedText();
                finish();
                break;
            case R.id.button_copy:
                copySelectedText();
                finish();
                break;
            case R.id.button_wiki:
                wikiSelectedText();
                finish();
                break;
            default:
                break;
        }
    }
}
