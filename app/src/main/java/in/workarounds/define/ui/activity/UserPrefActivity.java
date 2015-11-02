package in.workarounds.define.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import in.workarounds.define.R;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by madki on 13/10/15.
 */
public class UserPrefActivity extends BaseActivity implements View.OnClickListener {
    public static final int OPTION_SILENT   = 1;
    public static final int OPTION_PRIORITY = 2;
    public static final int OPTION_DIRECT   = 3;

    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prefs);

        init();

        View nextButton = findViewById(R.id.btn_next);
        if(!PrefUtils.getSettingsDone(this)){
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
        }
        nextButton.setOnClickListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected String getToolbarTitle() {
        return "Settings";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.rb_option_silent ||
                id == R.id.rb_option_direct ||
                id == R.id.rb_option_priority ) {
            boolean checked = ((RadioButton) v).isChecked();
            if(checked) {
                switch (id) {
                    case R.id.rb_option_direct:
                        PrefUtils.setNotifyMode(OPTION_DIRECT, this);
                        description.setText(R.string.notify_tutorial_direct);
                        break;
                    case R.id.rb_option_priority:
                        PrefUtils.setNotifyMode(OPTION_PRIORITY, this);
                        description.setText(R.string.notify_tutorial_priority);
                        break;
                    case R.id.rb_option_silent:
                        PrefUtils.setNotifyMode(OPTION_SILENT, this);
                        description.setText(R.string.notify_tutorial_silent);
                        break;
                }
            }
        } else if(id == R.id.button_test) {
            demoNotificationMode();
        } else if(id == R.id.btn_next){
            next();
        } else if(id == R.id.notification_autocancel_checkbox){
            PrefUtils.setNotificationAutoHideFlag(((CheckBox) v).isChecked(), this);
        }
    }

    private void init(){
        RadioButton direct = (RadioButton) findViewById(R.id.rb_option_direct);
        RadioButton silent = (RadioButton) findViewById(R.id.rb_option_silent);
        RadioButton priority = (RadioButton) findViewById(R.id.rb_option_priority);
        CheckBox notificationAutoHide = (CheckBox) findViewById(R.id.notification_autocancel_checkbox);
        description = (TextView) findViewById(R.id.tv_mode_description);

        boolean belowLollipop = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
        if(belowLollipop){
            priority.setVisibility(View.GONE);
        }

        int notifyMode = PrefUtils.getNotifyMode(this);
        switch(notifyMode){
            case OPTION_DIRECT:
                direct.setChecked(true);
                description.setText(R.string.notify_tutorial_direct);
                break;
            case OPTION_PRIORITY:
                priority.setChecked(true);
                description.setText(R.string.notify_tutorial_priority);
                break;
            case OPTION_SILENT:
                priority.setChecked(true);
                description.setText(R.string.notify_tutorial_silent);
                break;
            default:
                break;
        }

        direct.setOnClickListener(this);
        priority.setOnClickListener(this);
        silent.setOnClickListener(this);
        notificationAutoHide.setOnClickListener(this);

        notificationAutoHide.setChecked(PrefUtils.getNotificationAutoHideFlag(this));
        findViewById(R.id.button_test).setOnClickListener(this);
    }

    public void next(){
        PrefUtils.setSettingsDone(true, this);
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finishOnStop = true;
    }

    private void demoNotificationMode(){
        int notifyMode = PrefUtils.getNotifyMode(this);
        switch (notifyMode){
            case OPTION_PRIORITY:
                copyTutorialText(R.string.notify_tutorial_priority);
                break;
            case OPTION_SILENT:
                copyTutorialText(R.string.notify_tutorial_silent);
                break;
            case OPTION_DIRECT:
                copyTutorialText(R.string.notify_tutorial_direct);
                break;
            default:
                break;
        }
    }

    private void copyTutorialText(@StringRes int stringRes){
        String notifyTutorial = getString(stringRes);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Define", notifyTutorial);
        clipboard.setPrimaryClip(clip);
    }

    @IntDef({OPTION_DIRECT, OPTION_PRIORITY, OPTION_SILENT})
    public @interface NotifyMode {
    }
}
