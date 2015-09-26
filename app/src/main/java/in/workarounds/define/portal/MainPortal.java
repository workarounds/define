package in.workarounds.define.portal;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import in.workarounds.define.R;
import in.workarounds.define.network.DaggerNetworkComponent;
import in.workarounds.define.network.NetworkModule;
import in.workarounds.define.ui.view.MeaningPage;
import in.workarounds.define.ui.view.SelectableTextView;
import in.workarounds.define.util.LogUtils;
import in.workarounds.portal.Portal;

/**
 * Created by madki on 20/09/15.
 */
public class MainPortal extends Portal implements ComponentProvider {
    private static final String TAG = LogUtils.makeLogTag(MainPortal.class);
    public static final String BUNDLE_KEY_CLIP_TEXT = "bundle_key_clip_text";
    private String mClipText;
    private SelectableTextView mTvClipText;
    private View mPortalContainer;
    private PortalComponent component;
    private MeaningPage meaningPage;


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
                .networkComponent(DaggerNetworkComponent.builder().networkModule(new NetworkModule(this)).build())
                .build();
    }

    @Override
    public PortalComponent component() {
        return component;
    }

    private void initViews() {
        mPortalContainer = findViewById(R.id.ll_main_portal_container);
        mTvClipText = (SelectableTextView) findViewById(R.id.tv_clip_text);

        mPortalContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        meaningPage = (MeaningPage) findViewById(R.id.meaning_container);

        mTvClipText.setOnWordSelectedListener(new SelectableTextView.OnWordSelectedListener() {
            @Override
            public void onWordSelected(String word) {
                Toast.makeText(MainPortal.this, "Word clicked: " + word, Toast.LENGTH_LONG).show();
                meaningPage.setWord(word);
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


}
