package in.workarounds.define.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import in.workarounds.define.service.ChatHeadService;

/**
 * Created by manidesto on 24/05/15.
 */
public class TransparentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        Intent serviceIntent = new Intent(this, ChatHeadService.class);
        serviceIntent.putExtras(extras);
        startService(serviceIntent);
        finish();
    }
}
