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

        if(!PermissionsActivity.areRequiredPermissionGranted(this)){
            Intent permissions = PermissionsActivity.fromSplash(this);
            permissions.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(permissions);
            finish();
            return;
        }

        if(!PrefUtils.getDictionariesDone(this)){
            Intent dictionaries = new Intent(this, DictionariesActivity.class);
            dictionaries.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(dictionaries);
            finish();
            return;
        }

        if(!PrefUtils.getTutorialDone(this)){
            Intent tutorial = new Intent(this, TutorialActivity.class);
            tutorial.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(tutorial);
            finish();
            return;
        }

        if(!PrefUtils.getSettingsDone(this)){
            Intent settings = new Intent(this, UserPrefActivity.class);
            settings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(settings);
            finish();
            return;
        }

        Intent dashboard = new Intent(this, DashboardActivity.class);
        startActivity(dashboard);
        finish();
    }
}
