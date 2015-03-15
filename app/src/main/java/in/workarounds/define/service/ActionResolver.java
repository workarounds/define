package in.workarounds.define.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;

import in.workarounds.define.R;
import in.workarounds.define.ui.adapter.CardHandler;
import in.workarounds.define.ui.view.PopupRoot;
import in.workarounds.define.util.LogUtils;

/**
 * Controls the popup views when the clipboard service detects that a text is
 * copied
 * 
 * @author manidesto
 * 
 */
public class ActionResolver extends Service implements PopupRoot.OnCloseDialogsListener {
    private static final String TAG = LogUtils.makeLogTag(ActionResolver.class);

    public static final String INTENT_EXTRA_CLIPTEXT = "clip_text";

	/**
	 * Window manager object which will retrieved from the clipboard service to
	 * draw over other apps
	 */
	private WindowManager mWindowManager;

	/**
	 * Root view that contains all the views of the popup. Its obtained by
	 * inflating popup.xml
	 */
	private View mPopupRoot;

	/**
	 * The object that handles all the touches and views in the cards
	 */
	private CardHandler mCardHandler;

	/**
	 * State variable that tells whether the root view is added to the window
	 * manager or not. This variable helps us avoid run time exceptions that get
	 * triggered when a view is added to a window manager multiple times.
	 */
	private static boolean isRootViewAdded = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	/**
	 * Generates the LayoutParams for our window, which tells the system the
	 * this window should be drawn over all other layers(like a chat head)
	 * 
	 * @return LayoutParams object for a chat head like functionality
	 */
	private LayoutParams getRootViewParams() {
		LayoutParams params = new LayoutParams();
		params.type = LayoutParams.TYPE_PRIORITY_PHONE;
		params.flags = params.flags | LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| LayoutParams.FLAG_DIM_BEHIND;
		params.dimAmount = 0.5f;
		params.gravity = Gravity.TOP;
		params.format = PixelFormat.TRANSLUCENT;

		return params;
	}

	/**
	 * sets up touch listeners for the popup and card so that touch outside the
	 * card dismisses the popup and touch on the card consumes the touch
	 */
	private void initPopupListeners() {
		ViewGroup popup = (ViewGroup) mPopupRoot.findViewById(R.id.popup);
		ViewGroup card = (ViewGroup) popup.findViewById(R.id.bubble_card);

		popup.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent me) {
				int action = me.getAction();
				switch (action) {
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					removePopup();
					v.performClick();
				default:

				}
				return true;
			}
		});
		mCardHandler = new CardHandler(this);
		card.setOnTouchListener(mCardHandler);

		PopupRoot popupRoot = (PopupRoot) mPopupRoot;
		popupRoot.registerOnCloseDialogsListener(this);
	}

	/**
	 * Adds the root view to the window manager if not already added and
	 * initializes the touch listeners
	 */
	private void addRootView() {
		if (isRootViewAdded)
			return;
		LayoutInflater inflater = getInflater();
		LayoutParams params = getRootViewParams();
		if (mPopupRoot == null) {
			// needed to resolve layout params of inflated layout
			ViewGroup fakeRoot = new RelativeLayout(this);
			mPopupRoot = inflater.inflate(R.layout.popup, fakeRoot, false);
		}
		mWindowManager.addView(mPopupRoot, params);
		initPopupListeners();
		mPopupRoot.requestFocus();
		isRootViewAdded = true;
	}

	/**
	 * Removes the root view from the window manager if already added and stop
	 * the PopupManager service;
	 */
	private void removeRootView() {
		if (isRootViewAdded) {
			mWindowManager.removeViewImmediate(mPopupRoot);
			isRootViewAdded = false;
		}
		stopSelf();
	}
	
	public static boolean isPopupShown(){
		return isRootViewAdded; 
	}

	/**
	 * Function that is called by the ClipboardService when a text is copied
	 * 
	 * @param clipText
	 *            text that is copied into the clipboard
	 */
	public void handleClipText(String clipText) {
		String[] words = clipText.split(" ");

		if (words.length > 1) {
			addRootView();
			mCardHandler.showBubbles(words);
		} else {
			addRootView();
			mPopupRoot.findViewById(R.id.bubble_card).setVisibility(View.GONE);
			mCardHandler.showMeanings(words[0]);
		}
	}

	/**
	 * public function that should be called by the ClipboardService or other
	 * listeners to remove the pop up when necessary. For Example, the pop up
	 * should be removed when any navigation keys are pressed.
	 */
	public void removePopup() {
		removeRootView();
		stopSelf();
		// Do necessary callbacks to ClipboardService
	}

	public void hidePopup() {
		// Currently only removes the popup, ideally should hide it like a
		// partially visible card
		removePopup();
	}

	/**
	 * Listener method for OnCloseDialogsListener interface. Called when user
	 * presses one of the BACK, HOME or RECENT(MULTI-TASKING) buttons
	 */
	@Override
	public void onCloseDialogs() {
		hidePopup();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String clipText = intent.getStringExtra(INTENT_EXTRA_CLIPTEXT);
		handleClipText(clipText);
		return START_NOT_STICKY;
	}

	/**
	 * Used to get the layout inflater system service to inflate views in the
	 * pop up
	 * 
	 * @return LayoutInflater object
	 */
	public LayoutInflater getInflater() {
		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater;
	}

	/**
	 * Public function to get the card viewgroup. Used in card handler.
	 * 
	 * @return card ViewGroup
	 */
	public ViewGroup getBubbleCardView() {
		ViewGroup card = (ViewGroup) mPopupRoot.findViewById(R.id.bubble_card);
		return card;
	}

	/**
	 * Returns the ScrollView which encompasses the bubble card
	 * 
	 * @return the ScrollView which encompasses the bubble card
	 */
	private View getBubbleCardParentView() {
		View parent = mPopupRoot.findViewById(R.id.bubble_card_parent);
		return parent;
	}

	/**
	 * Sets the height of the ScrollView that contains the bubble card
	 * dynamically. This basically emulates MAX-HEIGHT functionality for the
	 * scroll view. The max-height is accessed from the dimension resources
	 */
	public void setBubbleCardScrollHeight() {
		View scroll = getBubbleCardParentView();
		View card = getBubbleCardView();
		int maxHeight = getResources().getDimensionPixelSize(
				R.dimen.bubble_card_height);
		int width = getResources().getDisplayMetrics().widthPixels;
		int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		card.measure(widthSpec, MeasureSpec.UNSPECIFIED);
		if (card.getMeasuredHeight() > maxHeight) {
			ViewGroup.LayoutParams params = scroll.getLayoutParams();
			params.height = maxHeight;
			scroll.setLayoutParams(params);
		}
	}
	
	public View getRootView(){
		return mPopupRoot;
	}

	/**
	 * returns the Meaning Card ViewGroup
	 * 
	 * @return
	 */
	public ViewGroup getMeaningCard() {
		ViewGroup meaningCard = (ViewGroup) mPopupRoot
				.findViewById(R.id.meaning_card);
		return meaningCard;
	}
}
