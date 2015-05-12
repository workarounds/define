package in.workarounds.define.service;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import in.workarounds.define.R;

/**
 * Created by manidesto on 13/05/15.
 */
public class TestUIService extends UIService{
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        goToState(STATE_BUBBLE);
        return START_NOT_STICKY;
    }

    @Override
    protected View onCreateBubble() {
        View bubbleView = LayoutInflater.from(this).inflate(R.layout.layout_test_bubble, null, false);
        bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCard();
            }
        });
        return bubbleView;
    }

    @Override
    protected View onCreateCard() {
        View cardView = LayoutInflater.from(this).inflate(R.layout.layout_test_card, null, false);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
        return cardView;
    }

    private void toggleCard(){
        if(getState() == STATE_BUBBLE){
            goToState(STATE_CARD);
        } else {
            goToState(STATE_BUBBLE);
        }
    }
}
