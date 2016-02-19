package in.workarounds.define.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import in.workarounds.define.R;
import in.workarounds.define.portal.MeaningPortal;
import in.workarounds.define.portal.PortalId;
import in.workarounds.define.service.DefinePortalService;
import in.workarounds.portal.Portals;

public class ProcessTextActivity extends AppCompatActivity{
    CharSequence text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_view);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            text = getIntent()
                    .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(!TextUtils.isEmpty(text)) {
            Bundle bundle = new Bundle();
            bundle.putString(MeaningPortal.BUNDLE_KEY_CLIP_TEXT, text.toString());
            Portals.open(PortalId.MEANING_PORTAL, bundle, this, DefinePortalService.class);
        }
        finish();
    }
}
