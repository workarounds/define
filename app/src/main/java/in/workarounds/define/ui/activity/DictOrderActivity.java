package in.workarounds.define.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import in.workarounds.define.R;

/**
 * Created by madki on 27/10/15.
 */
public class DictOrderActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_order);
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
        return "Order Dictionaries";
    }

}
