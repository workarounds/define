package in.workarounds.define.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseArray;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import in.workarounds.define.R;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by manidesto on 15/05/15.
 */
public class TestActivity extends ActionBarActivity {
    public static final String KEY_USE_UI_SERVICE = "key_use_ui_service";
    private SparseArray<String> mToggles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mToggles = new SparseArray<>(2);
        mToggles.append(R.id.checkbox_ui_service, KEY_USE_UI_SERVICE);
        mToggles.append(R.id.checkbox_notification_only, PrefUtils.KEY_NOTIF_ONLY);

        for(int i = 0; i < mToggles.size(); i++) {
            CheckBox checkBox = (CheckBox) findViewById(mToggles.keyAt(i));
            final String value = mToggles.valueAt(i);
            boolean setting = PrefUtils.getSharedPreferences(this)
                    .getBoolean(value, false);
            checkBox.setChecked(setting);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    PrefUtils.getSharedPreferences(TestActivity.this)
                            .edit()
                            .putBoolean(value, isChecked)
                            .apply();
                }
            });
        }
    }
}
