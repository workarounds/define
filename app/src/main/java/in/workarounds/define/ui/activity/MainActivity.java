package in.workarounds.define.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

import in.workarounds.define.BuildConfig;
import in.workarounds.define.R;
import in.workarounds.define.api.Constants;
import in.workarounds.define.helper.DownloadProgressThread;
import in.workarounds.define.helper.DownloadResolver;
import in.workarounds.define.helper.FileHelper;
import in.workarounds.define.helper.SpeechHelper;
import in.workarounds.define.service.ClipboardService;
import in.workarounds.define.service.UnzipService;
import in.workarounds.define.util.LogUtils;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    /**
     * constants used in messenger
     */
    public static final int MSG_UNZIP_PROGRESS = 1;
    /**
     * Messenger that delivers messages to UnzipService when bound
     */
    private Messenger mUnzipService;
    /**
     * messenger to be passed to the bound service
     */
    private Messenger mMessenger = new Messenger(new IncomingHandler(this));
    /**
     * flag to see if the activity is bound to the service
     */
    private boolean mBound;
    /**
     * Speech helper that starts and manages the TTS object
     */
    private SpeechHelper mSpeechHelper;
    private EditText mSelectedWord ;
    private DownloadProgressThread mThread;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mUnzipService = new Messenger(service);
            mBound = true;

            Message msg = Message.obtain(null, UnzipService.MSG_REGISTER, mMessenger);
            sendToUnzipService(msg);
        }

        public void onServiceDisconnected(ComponentName className) {
            mUnzipService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        mSpeechHelper = new SpeechHelper(this);

        ActionBar actionBar = getSupportActionBar();
        int themeColor = getResources().getColor(R.color.theme_primary);
        Drawable bg = new ColorDrawable(themeColor);
        actionBar.setBackgroundDrawable(bg);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mThread = DownloadResolver.setUpProgress(Constants.WORDNET, progressBar, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        if(BuildConfig.DEBUG){
            inflater.inflate(R.menu.menu_debug, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_test:
                startActivity(new Intent(this, TestActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * starts the clipboard service if its not up already
     */
    private void startClipboardService() {
        boolean serviceUp = ClipboardService.isRunning();
        if (!serviceUp) {
            Intent intent = new Intent(getBaseContext(), ClipboardService.class);
            getBaseContext().startService(intent);
        }
    }

    /**
     * creates a folder "Define" in sd card if not present, generates absolute
     * paths and files to /sdcard/Recopy/dict.zip and /sdcard/Recopy/dict/
     */
    private void initFiles() {
        FileHelper.createRootFile();
    }

    @Override
    protected void onStart() {
        startClipboardService();
        super.onStart();
        bindToUnzipService();
    }


    @Override
    protected void onResume() {
        startClipboardService();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromUnzipService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechHelper.destroy();
        if(mThread != null) {
            mThread.close();
        }
    }

    /**
     * function called on click of "Download Data" button calls
     * checkNetworkStatus and takes appropriate action
     * @param v
     */
    public void downloadClick(View v) {
        DownloadResolver.startDownload(Constants.WORDNET, this);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mThread = DownloadResolver.setUpProgress(Constants.WORDNET, progressBar, null, this);
    }

    /**
     * button's function to cancel ongoing download
     *
     * @param v
     */
    public void cancelClick(View v) {
        DownloadResolver.cancelDownload(Constants.WORDNET, this);
    }

    public void meaningClick(View v) {
        Message msg = Message.obtain(null, UnzipService.MSG_UNZIP);
        sendToUnzipService(msg);
    }

    public void speakAmerican(View v) {
        mSpeechHelper.speakAmerican(mSelectedWord.getText().toString());
    }

    public void speakBritish(View v) {
        mSpeechHelper.speakBritish(mSelectedWord.getText().toString());
    }

    private void setup() {
        mSelectedWord = (EditText) findViewById(R.id.edit_message);
    }

    /**
     * helper method to bind to UnzipService
     */
    private void bindToUnzipService() {
        // Bind to the service
        bindService(new Intent(this, UnzipService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    /**
     * helper method to unbind from the UnzipService
     */
    private void unbindFromUnzipService() {
        // Unbind from the service
        if (mBound) {
            Message msg = Message.obtain(null, UnzipService.MSG_UNREGISTER);
            sendToUnzipService(msg);
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * helper method to send message to unzip service
     * @param message to be sent to UnzipService
     */
    private void sendToUnzipService(Message message) {
        if(!mBound) {
            LogUtils.LOGE(TAG, "Service not bound");
        } else {
            try {
                mUnzipService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    private static class IncomingHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        public IncomingHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if(activity != null) {
                switch (msg.what) {
                    case MSG_UNZIP_PROGRESS:
                        LogUtils.LOGD(TAG, "Unzip progress: " + msg.arg1);
                        break;
                    default:
                        super.handleMessage(msg);
                }
            } else {
                LogUtils.LOGE(TAG, "No activity passed to handler");
                super.handleMessage(msg);
            }
        }
    }

}
