package in.workarounds.define.portal;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import in.workarounds.define.R;
import in.workarounds.define.base.MeaningPagerAdapter;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.network.DaggerNetworkComponent;
import in.workarounds.define.network.NetworkModule;
import in.workarounds.define.service.ClipboardService;
import in.workarounds.define.ui.activity.DashboardActivity;
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
    private static final int SELECTION_CARD_ANIMATION_TIME = 350;
    private static final int MEANING_PAGE_ANIMATION_TIME = 350;
    private String mClipText;

    private SelectableTextView mTvClipText;
    private ViewPager pager;

    private String selectedText;

    private View mPortalContainer;
    private View meaningPagesContainer;
    private View selectionCard;
    private PortalComponent component;

    private List<MeaningPresenter> presenters = new ArrayList<>();

    private CallStateListener callStateListener;
    private TelephonyManager telephonyManager;

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
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        callStateListener = new CallStateListener();
    }

    @Override
    protected void onData(Bundle data) {
        super.onData(data);
        extractClipText(data);
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
        selectionCard = findViewById(R.id.cv_action_card);
        selectionCard.setVisibility(View.GONE);
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
            slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.white));
            slidingTabLayout.setCustomTabView(R.layout.layout_sliding_tabs, R.id.tv_tab_header);
            slidingTabLayout.setViewPager(pager);
        }

        mTvClipText.setOnWordSelectedListener(new SelectableTextView.OnWordSelectedListener() {
            @Override
            public void onWordSelected(String word) {
                selectedText = word;
                animateMeaningsContainer();
                for (MeaningPresenter presenter : presenters) {
                    presenter.onWordUpdated(word);
                }
            }
        });
        initButtons();
        selectionCard.post(new Runnable() {
            @Override
            public void run() {
                animateSelectionCard();
            }
        });
        //pre-drawing the meaning container to reduce lag
        selectionCard.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(meaningPagesContainer.getVisibility() == View.GONE) {
                    meaningPagesContainer.setVisibility(View.INVISIBLE);
                }
            }
        }, SELECTION_CARD_ANIMATION_TIME + 50);
    }

    private void animateSelectionCard(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int translation = getResources().getDimensionPixelSize(R.dimen.bubble_card_height);
            selectionCard.setAlpha(0);
            selectionCard.setTranslationY(-translation);
            selectionCard.setVisibility(View.VISIBLE);
            selectionCard.animate()
                    .alpha(1)
                    .withLayer()
                    .translationY(0)
                    .setDuration(SELECTION_CARD_ANIMATION_TIME)
                    .setInterpolator(new DecelerateInterpolator(3))
                    .start();
        } else {
            selectionCard.setVisibility(View.VISIBLE);
        }
    }

    private void animateMeaningsContainer(){
        if(meaningPagesContainer.getVisibility() != View.VISIBLE){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                meaningPagesContainer.setAlpha(0);
                meaningPagesContainer.setTranslationY(meaningPagesContainer.getHeight() / 3);
                meaningPagesContainer.setVisibility(View.VISIBLE);
                meaningPagesContainer.animate()
                        .alpha(1)
                        .withLayer()
                        .translationY(0)
                        .setDuration(MEANING_PAGE_ANIMATION_TIME)
                        .setInterpolator(new DecelerateInterpolator(4))
                        .start();
            } else {
                meaningPagesContainer.setVisibility(View.VISIBLE);
            }
        }
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
            BreakIterator iterator = BreakIterator.getWordInstance();
            iterator.setText(mClipText);
            iterator.first();
            int n = 0;
            for(int end = iterator.next(); end != BreakIterator.DONE; end = iterator.next()){
                n++;
            }
            if(n <= 2){
                mTvClipText.selectAll();
            }
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
        findViewById(R.id.button_define).setOnClickListener(this);
        findViewById(R.id.button_search).setOnClickListener(this);
        findViewById(R.id.button_copy).setOnClickListener(this);
        findViewById(R.id.button_wiki).setOnClickListener(this);
    }

    private void openDefineApp(){
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void copySelectedText() {
        if(TextUtils.isEmpty(selectedText)) return;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Define", selectedText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();
    }

    private void searchSelectedText() {
        String textToSearch = mClipText;
        if(!TextUtils.isEmpty(selectedText)){
            textToSearch = selectedText;
        }
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, textToSearch);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void wikiSelectedText() {
        String textToSearch = mClipText;
        if(!TextUtils.isEmpty(selectedText)){
            textToSearch = selectedText;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://en.m.wikipedia.org/wiki/" + textToSearch));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        finish();
    }

    @Override
    protected void onResume() {
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
       int id = v.getId();
        switch (id) {
            case R.id.button_define:
                openDefineApp();
                finish();
                break;
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

    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone
                    NotificationUtils.INSTANCE.sendSilentMeaningNotification(mTvClipText.getText().toString(),
                            ClipboardService.SILENT_NOTIFICATION_ID);
                    finish();
                    break;
            }
        }
    }
}
