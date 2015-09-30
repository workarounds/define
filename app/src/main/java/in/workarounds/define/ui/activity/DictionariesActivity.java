package in.workarounds.define.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.workarounds.define.R;
import in.workarounds.define.api.Constants;
import in.workarounds.define.file.unzip.UnzipHandler;
import in.workarounds.define.file.unzip.UnzipService;
import in.workarounds.define.helper.DownloadProgressThread;
import in.workarounds.define.helper.DownloadResolver;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.ViewUtils;

/**
 * Created by madki on 30/09/15.
 */
public class DictionariesActivity extends BaseActivity implements UnzipHandler.HandlerCallback, View.OnClickListener {
    private static final String TAG = LogUtils.makeLogTag(DictionariesActivity.class);
    /**
     * Messenger that delivers messages to UnzipService when bound
     */
    private Messenger mUnzipService;
    /**
     * messenger to be passed to the bound service
     */
    private Messenger mMessenger = new Messenger(new UnzipHandler(this));
    /**
     * flag to see if the activity is bound to the service
     */
    private boolean mBound;
    /**
     * Thread that listens and updates download progress
     */
    private DownloadProgressThread mThread;

    private ProgressBar unzipProgress;
    private ProgressBar downloadProgress;
    private TextView statusTv;
    private Button downloadButton;
    private Button cancelButton;

    @Override
    protected void onStart() {
        super.onStart();
        bindToUnzipService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionaries);
        setToolbarTitle(R.string.app_name);

        statusTv = (TextView) findViewById(R.id.tv_progress_status);
        unzipProgress = (ProgressBar) findViewById(R.id.pb_unzip);
        downloadProgress = (ProgressBar) findViewById(R.id.pb_download);

        ViewUtils.setColorOfProgressBar(unzipProgress, ContextCompat.getColor(this, R.color.theme_primary));
        ViewUtils.setColorOfProgressBar(downloadProgress, ContextCompat.getColor(this, R.color.theme_primary));

        downloadButton = (Button) findViewById(R.id.btn_download_wordnet);
        cancelButton = (Button) findViewById(R.id.btn_cancel_download_wordnet);

        downloadButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        mThread = DownloadResolver.setUpProgress(Constants.WORDNET, downloadProgress, statusTv, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromUnzipService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        unzipProgress.setVisibility(View.GONE);
        statusTv.setVisibility(View.GONE);
    }

    @Override
    protected String getToolbarTitle() {
        return "Dictionaries";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mThread != null) {
            mThread.close();
        }
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

    /**
     * helper method to bind to UnzipService
     */
    private void bindToUnzipService() {
        // Bind to the service
        bindService(new Intent(this, UnzipService.class), mConnection,
                Context.BIND_AUTO_CREATE);
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

    @Override
    public void onUnzipProgressUpdate(int progress) {
        statusTv.setVisibility(View.VISIBLE);
        downloadProgress.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);

        if(progress > 0 && progress < 100) {
            statusTv.setText("Unzipping");
            unzipProgress.setVisibility(View.VISIBLE);
            unzipProgress.setProgress(progress);
        } else if(progress == 100) {
            unzipProgress.setVisibility(View.GONE);
            statusTv.setText("Finished downloading dictionary");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_download_wordnet:
                downloadClick();
                break;
            case R.id.btn_cancel_download_wordnet:
                cancelClick();
                break;
            default:
                LogUtils.LOGE(TAG, "No actionable define for this click");
        }
    }

    /**
     * function called on click of "Download Data" button calls
     * checkNetworkStatus and takes appropriate action
     */
    private void downloadClick() {
        downloadButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.VISIBLE);
        DownloadResolver.startDownload(Constants.WORDNET, this);
        mThread = DownloadResolver.setUpProgress(Constants.WORDNET, downloadProgress, statusTv, this);
    }

    /**
     * button's function to cancel ongoing download
     */
    public void cancelClick() {
        cancelButton.setVisibility(View.GONE);
        downloadButton.setVisibility(View.VISIBLE);

        downloadProgress.setVisibility(View.GONE);
        unzipProgress.setVisibility(View.GONE);
        DownloadResolver.cancelDownload(Constants.WORDNET, this);
    }

}
