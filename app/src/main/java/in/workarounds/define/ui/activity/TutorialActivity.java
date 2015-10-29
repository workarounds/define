package in.workarounds.define.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import in.workarounds.define.R;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by manidesto on 24/10/15.
 */
public class TutorialActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        findViewById(R.id.button_copy).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
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
        return getString(R.string.app_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.setTutorialDone(true, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_copy:
                onCopyClicked();
                break;
            case R.id.btn_next:
                next();
                break;
        }
    }

    public void next(){
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    public void onCopyClicked(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Define", getString(R.string.selection));
        clipboard.setPrimaryClip(clip);
    }
}
