package in.workarounds.define.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import in.workarounds.define.R;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by manidesto on 24/10/15.
 */
public class SplashActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(!PrefUtils.getDictionariesDone(this)){
            Intent dictionaries = new Intent(this, DictionariesActivity.class);
            startActivity(dictionaries);
            finish();
            return;
        }

        if(!PrefUtils.getTutorialDone(this)){
            Intent tutorial = new Intent(this, TutorialActivity.class);
            startActivity(tutorial);
            finish();
            return;
        }

        Intent dashboard = new Intent(this, DashboardActivity.class);
        startActivity(dashboard);
        finish();
    }
}
