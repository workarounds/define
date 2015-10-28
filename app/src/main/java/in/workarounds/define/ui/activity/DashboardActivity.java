package in.workarounds.define.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import in.workarounds.define.R;

/**
 * Created by manidesto on 26/10/15.
 */
public class DashboardActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.dictionaries_container).setOnClickListener(this);
        findViewById(R.id.tutorial_container).setOnClickListener(this);
        findViewById(R.id.settings_container).setOnClickListener(this);
        findViewById(R.id.sort_dictionaries_container).setOnClickListener(this);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dictionaries_container:
                startActivity(getDictionariesIntent());
                break;
            case R.id.tutorial_container:
                startActivity(getTutorialIntent());
                break;
            case R.id.sort_dictionaries_container:
                startActivity(getSortDictionariesIntent());
                break;
            case R.id.settings_container:
                startActivity(getSettingsIntent());
                break;
        }
    }

    private Intent getDictionariesIntent(){
        return new Intent(this, DictionariesActivity.class);
    }

    private Intent getTutorialIntent(){
        return new Intent(this, TutorialActivity.class);
    }

    private Intent getSortDictionariesIntent(){
        return new Intent(this, DictOrderActivity.class);
    }

    private Intent getSettingsIntent(){
        return new Intent(this, UserPrefActivity.class);
    }
}
