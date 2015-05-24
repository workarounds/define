package in.workarounds.define.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import in.workarounds.define.R;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by manidesto on 15/05/15.
 */
public class TestActivity extends ActionBarActivity {
    public static final String KEY_USE_UI_SERVICE = "key_use_ui_service";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_ui_service);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefUtils.getSharedPreferences(TestActivity.this)
                        .edit()
                        .putBoolean(KEY_USE_UI_SERVICE, isChecked)
                        .apply();
            }
        });
    }
}
