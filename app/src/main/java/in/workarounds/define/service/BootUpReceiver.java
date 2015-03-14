package in.workarounds.define.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.d("testing", "recieved broadcast");
		boolean serviceUp = ClipboardService.isRunning();
		if(!serviceUp){
			Intent intent = new Intent(arg0, ClipboardService.class);
			arg0.startService(intent);
		}
	}

}
