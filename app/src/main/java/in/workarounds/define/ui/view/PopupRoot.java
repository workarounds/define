package in.workarounds.define.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class PopupRoot extends RelativeLayout {
	
	public interface OnCloseDialogsListener{
		public void onCloseDialogs();
	}
	
	private OnCloseDialogsListener listener;

	public PopupRoot(Context context) {
		super(context);
	}

	public PopupRoot(Context context, AttributeSet attrs,
                     int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public PopupRoot(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void registerOnCloseDialogsListener(OnCloseDialogsListener l){
		listener = l;
	}
	
	public void removeOnCloseDialogsListener(){
		listener = null;
	}
	
	private void sendCallbackToListener(){
		if(listener != null) listener.onCloseDialogs();
	}
	
	public void onCloseSystemDialogs(String reason){
		sendCallbackToListener();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) sendCallbackToListener();
		return super.dispatchKeyEvent(event);
	}
	
	

}
