package in.workarounds.define.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import in.workarounds.define.R;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by madki on 27/10/15.
 */
public class DictOrderActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict_order);
        View next = findViewById(R.id.btn_next);
        next.setOnClickListener(this);

        if(PrefUtils.getSortDone(this)){
            next.setVisibility(View.GONE);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        PrefUtils.setSortDone(true, this);
    }

    public void next(){
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finishOnStop = true;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_next:
                next();
                break;
            default:
                break;
        }
    }
}
