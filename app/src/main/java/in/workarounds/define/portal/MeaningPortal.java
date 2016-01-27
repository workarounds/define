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
import in.workarounds.portal.MainPortal;
import timber.log.Timber;

/**
 * Created by madki on 20/09/15.
 */
public class MeaningPortal extends MainPortal<DefinePortalAdapter> implements ComponentProvider, SelectionCardController, MeaningsController {
    public static final String BUNDLE_KEY_CLIP_TEXT = "bundle_key_clip_text";
    private static final int SELECTION_CARD_ANIMATION_TIME = 350;
    private static final int MEANING_PAGE_ANIMATION_TIME = 350;

    private ViewPager pager;

    private View mPortalContainer;
    private View meaningPagesContainer;
    private View selectionCard;
    private PortalComponent component;
    private MeaningPagerAdapter pagerAdapter;

    private CallStateListener callStateListener;
    private TelephonyManager telephonyManager;

    // to remember state, so that animations won't take place
    // when rotated. Animation in happens only once
    private boolean cardAnimatedIn = false;
    private boolean meaningsAnimatedIn = false;

    @Inject
    SelectionCardPresenter selectionCardPresenter;
    @Inject
    NotificationUtils notificationUtils;

    String selectedText;
    String clipText;
    private Set<MeaningPresenter> meaningPresenters;

    public MeaningPortal(DefinePortalAdapter portalAdapter) {
        super(portalAdapter);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (portalAdapter != null) portalAdapter.startForeground();

        initComponents();
        meaningPresenters = new HashSet<>();

        pagerAdapter = new MeaningPagerAdapter(this);
        selectionCardPresenter.onClipTextChanged(extractClipText(bundle));
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        callStateListener = new CallStateListener();
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE);

        setContentView(R.layout.portal_main);
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        initViews();
    }

    @Override
    protected void onDetachView() {
        super.onDetachView();
    }


    @Override
    protected void onViewAttached() {
        super.onViewAttached();
    }


    @Override
    protected boolean onData(Bundle data) {
        super.onData(data);
        selectionCardPresenter.onClipTextChanged(extractClipText(data));
        return true;
    }

    @Override
    protected boolean onHomePressed() {
        finish();
        return true;
    }

    @Override
    protected boolean onRecentAppsPressed() {
        finish();
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
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

        mPortalContainer.setOnClickListener(v -> animateOutAndFinish());
        pager = (ViewPager) findViewById(R.id.vp_pages);
        pager.setAdapter(pagerAdapter);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_layout);
        if (slidingTabLayout != null) {
            slidingTabLayout.setDistributeEvenly((pagerAdapter.getCount() <= 3));
            slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.white));
            slidingTabLayout.setCustomTabView(R.layout.layout_sliding_tabs, R.id.tv_tab_header);
            slidingTabLayout.setViewPager(pager);
        }
        pager.setCurrentItem(pagerAdapter.getCurrentPosition());

        selectionCard.post(this::animateSelectionCardIn);
        //pre-drawing the meaning container to reduce lag
        selectionCard.postDelayed(() -> {
            if (meaningPagesContainer.getVisibility() == View.GONE) {
                meaningPagesContainer.setVisibility(View.INVISIBLE);
            }
        }, SELECTION_CARD_ANIMATION_TIME + 50);
    }

    private void animateSelectionCardIn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !cardAnimatedIn) {
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
            cardAnimatedIn = true;
        } else {
            selectionCard.setVisibility(View.VISIBLE);
        }
    }

    private void animateOutAndFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int translation = getResources().getDimensionPixelSize(R.dimen.bubble_card_height);
            ViewPropertyAnimator selectionCardAnimator = selectionCard.animate()
                    .alpha(0)
                    .withLayer()
                    .translationY(-translation)
                    .setDuration(SELECTION_CARD_ANIMATION_TIME)
                    .setInterpolator(new DecelerateInterpolator(3));
            ViewPropertyAnimator meaningPageAnimator = null;
            if (meaningPagesContainer.getVisibility() == View.VISIBLE) {
                meaningPageAnimator = meaningPagesContainer.animate()
                        .alpha(0)
                        .withLayer()
                        .translationY(meaningPagesContainer.getHeight() / 3)
                        .setDuration(MEANING_PAGE_ANIMATION_TIME);
            }
            selectionCardAnimator.start();
            if (meaningPageAnimator != null) {
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

    private Runnable finishRunnable = this::finish;

    private String extractClipText(Bundle bundle) {
        if (bundle != null && bundle.containsKey(BUNDLE_KEY_CLIP_TEXT)) {
            Timber.d("bundle clip text: " + bundle.getString(BUNDLE_KEY_CLIP_TEXT));
            clipText = bundle.getString(BUNDLE_KEY_CLIP_TEXT);
            return clipText;
        }
        return null;
    }


    private void showMeaningContainer() {
        if (meaningPagesContainer.getVisibility() != View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !meaningsAnimatedIn) {
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
                meaningsAnimatedIn = true;
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
        for (MeaningPresenter presenter : meaningPresenters) {
            presenter.onWordUpdated(word);
        }
    }

    @Override
    public void addMeaningPresenter(MeaningPresenter presenter) {
        boolean added = meaningPresenters.add(presenter);
        if (added) {
            if (selectedText != null) {
                presenter.onWordUpdated(selectedText);
            }
        } else {
            Timber.e("Presenter already added");
        }
    }

    @Override
    public void removeMeaningPresenter(MeaningPresenter presenter) {
        boolean removed = meaningPresenters.remove(presenter);
        if (!removed) {
            Timber.e("Presenter not added. Cannot remove");
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
        UtilPortal portlet = (UtilPortal) portalAdapter.getPortal(PortalId.UTIL_PORTAL);
        if (portlet != null)
            portlet.cancelCurrentNotificationClearer(); //remove notification hide handler
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
