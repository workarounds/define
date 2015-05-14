package in.workarounds.define.service;

import android.content.Intent;
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
    protected void onCreateBubble() {
        super.onCreateBubble();
        setBubbleView(R.layout.layout_test_bubble);
    }

    @Override
    protected void onBubbleCreated(View bubbleView) {
        super.onBubbleCreated(bubbleView);
        bubbleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToState(STATE_CARD);
            }
        });
    }

    @Override
    protected void onCreateCard() {
        super.onCreateCard();
        setCardView(R.layout.layout_test_card);
    }

    @Override
    protected void onCardCreated(View cardView) {
        super.onCardCreated(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToState(STATE_WAITING);
            }
        });
    }

    private void toggleCard(){
        if(getState() == STATE_BUBBLE){
            goToState(STATE_CARD);
        } else {
            goToState(STATE_BUBBLE);
        }
    }
}
