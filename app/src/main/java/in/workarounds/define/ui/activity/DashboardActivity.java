package in.workarounds.define.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import in.workarounds.define.R;

/**
 * Created by manidesto on 26/10/15.
 */
public class DashboardActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.dictionaries_container).setOnClickListener(dictionariesClickListener);
        findViewById(R.id.tutorial_container).setOnClickListener(tutorialClickListener);
        findViewById(R.id.settings_container).setOnClickListener(settingsClickListener);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.app_name);
    }

    private View.OnClickListener dictionariesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(getDictionariesIntent());
        }
    };

    private View.OnClickListener tutorialClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(getTutorialIntent());
        }
    };

    private View.OnClickListener settingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(getSettingsIntent());
        }
    };

    private Intent getDictionariesIntent(){
        return new Intent(this, DictionariesActivity.class);
    }

    private Intent getTutorialIntent(){
        return new Intent(this, TutorialActivity.class);
    }

    private Intent getSettingsIntent(){
        return new Intent(this, UserPrefActivity.class);
    }
}
