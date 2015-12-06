package in.workarounds.define.portal;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.base.MeaningPagerAdapter;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.base.NotificationUtils;
import in.workarounds.define.network.DaggerNetworkComponent;
import in.workarounds.define.network.NetworkModule;
import in.workarounds.define.ui.view.SelectionCard.SelectionCardController;
import in.workarounds.define.ui.view.SelectionCard.SelectionCardPresenter;
import in.workarounds.define.ui.view.slidingtabs.SlidingTabLayout;
import in.workarounds.define.util.LogUtils;
import in.workarounds.portal.Portal;

/**
 * Created by madki on 20/09/15.
 */
public class MainPortal extends Portal implements ComponentProvider, SelectionCardController, MeaningsController{
    private static final String TAG = LogUtils.makeLogTag(MainPortal.class);
    public static final String BUNDLE_KEY_CLIP_TEXT = "bundle_key_clip_text";
    private static final int SELECTION_CARD_ANIMATION_TIME = 350;
    private static final int MEANING_PAGE_ANIMATION_TIME = 350;

    private ViewPager pager;

    private View mPortalContainer;
    private View meaningPagesContainer;
    private View selectionCard;
    private PortalComponent component;

    private CallStateListener callStateListener;
    private TelephonyManager telephonyManager;

    @Inject
    SelectionCardPresenter selectionCardPresenter;
    @Inject
    NotificationUtils notificationUtils;

    String selectedText;
    String clipText;
    private Set<MeaningPresenter> meaningPresenters;

    public MainPortal(Context base) {
        super(base);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initComponents();
        meaningPresenters = new HashSet<>();
        setContentView(R.layout.portal_main);
        initViews();
        selectionCardPresenter.onClipTextChanged(extractClipText(bundle));
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        callStateListener = new CallStateListener();
    }

    @Override
    protected void onData(Bundle data) {
        super.onData(data);
        selectionCardPresenter.onClipTextChanged(extractClipText(data));
    }

    private void initComponents() {
        component = DaggerPortalComponent.builder()
                .portalModule(new PortalModule(this))
                .networkComponent(
                        DaggerNetworkComponent.builder()
                                .networkModule(new NetworkModule(this)).build())
                .build();
        component.inject(this);
    }

    @Override
    public PortalComponent component() {
        return component;
    }

    private void initViews() {
        mPortalContainer = findViewById(R.id.rl_main_portal_container);
        meaningPagesContainer = findViewById(R.id.ll_meaning_pages_container);
        selectionCard = findViewById(R.id.selection_card);
        selectionCard.setVisibility(View.GONE);
//        if (meaningPagesContainer != null) {
//            meaningPagesContainer.setVisibility(View.INVISIBLE);
//        }

        mPortalContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateOutAndFinish();
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

        selectionCard.post(new Runnable() {
            @Override
            public void run() {
                animateSelectionCardIn();
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

    private void animateSelectionCardIn(){
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

    private void animateOutAndFinish(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int translation = getResources().getDimensionPixelSize(R.dimen.bubble_card_height);
            ViewPropertyAnimator selectionCardAnimator = selectionCard.animate()
                    .alpha(0)
                    .withLayer()
                    .translationY(-translation)
                    .setDuration(SELECTION_CARD_ANIMATION_TIME)
                    .setInterpolator(new DecelerateInterpolator(3));
            ViewPropertyAnimator meaningPageAnimator = null;
            if(meaningPagesContainer.getVisibility() == View.VISIBLE){
                meaningPageAnimator = meaningPagesContainer.animate()
                        .alpha(0)
                        .withLayer()
                        .translationY(meaningPagesContainer.getHeight() / 3)
                        .setDuration(MEANING_PAGE_ANIMATION_TIME);
            }
            selectionCardAnimator.start();
            if(meaningPageAnimator != null) {
                meaningPageAnimator.start();
            }
            int animTime = Math.max(SELECTION_CARD_ANIMATION_TIME, MEANING_PAGE_ANIMATION_TIME);
            mPortalContainer.animate()
                    .alpha(0)
                    .withEndAction(finishRunnable)
                    .setDuration(animTime)
                    .withLayer()
                    .start();
        } else {
            finish();
        }
    }

    private Runnable finishRunnable = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    private String extractClipText(Bundle bundle) {
        if(bundle != null && bundle.containsKey(BUNDLE_KEY_CLIP_TEXT)) {
            LogUtils.LOGD(TAG, "bundle clip text: " + bundle.getString(BUNDLE_KEY_CLIP_TEXT));
            clipText = bundle.getString(BUNDLE_KEY_CLIP_TEXT);
            return clipText;
        }
        return null;
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

    private void showMeaningContainer() {
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

    @Override
    public void onWordSelected(String word) {
        onWordUpdated(word);
    }

    @Override
    public void onButtonClicked() {
        animateOutAndFinish();
    }

    @Override
    public void onWordUpdated(String word) {
        selectedText = word;
        showMeaningContainer();
        for(MeaningPresenter presenter : meaningPresenters) {
            presenter.onWordUpdated(word);
        }
    }

    @Override
    public void addMeaningPresenter(MeaningPresenter presenter) {
        boolean added = meaningPresenters.add(presenter);
        if(added) {
            if (selectedText != null) {
                presenter.onWordUpdated(selectedText);
            }
        } else {
            LogUtils.LOGE(TAG, "Presenter already added");
        }
    }

    @Override
    public void removeMeaningPresenter(MeaningPresenter presenter) {
        boolean removed = meaningPresenters.remove(presenter);
        if(!removed) {
            LogUtils.LOGE(TAG, "Presenter not added. Cannot remove");
        }
    }

    @Override
    public void onInstallClicked() {
        animateOutAndFinish();
    }

    @Override
    public void onDownloadClicked() {
        finishWithNotification();
    }

    public void finishWithNotification() {
        notificationUtils.sendSilentBackupNotification(clipText);
        animateOutAndFinish();
    }

    private class CallStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // called when someone is ringing to this phone
                    finishWithNotification();
                    break;
            }
        }
    }
}
