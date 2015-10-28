package in.workarounds.define.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import in.workarounds.define.R;
import in.workarounds.define.api.Constants;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.file.unzip.UnzipHandler;
import in.workarounds.define.file.unzip.UnzipService;
import in.workarounds.define.helper.DownloadProgressThread;
import in.workarounds.define.helper.DownloadResolver;
import in.workarounds.define.portal.ComponentProvider;
import in.workarounds.define.portal.PortalModule;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.define.util.ViewUtils;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;
import in.workarounds.define.webviewDicts.livio.LivioModule;
import in.workarounds.define.wordnet.WordnetDictionary;
import in.workarounds.define.wordnet.WordnetModule;

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
    private ImageView downloadButton;
    private ImageView cancelButton;
    private ImageView installLivioButton;
    private AsyncTask livioTask;
    private AsyncTask wordnetTask;
    private static final String testWord = "define";
    @Inject
    WordnetDictionary wordnetDictionary;
    @Inject
    LivioDictionary livioDictionary;

    @Override
    protected void onStart() {
        super.onStart();
        bindToUnzipService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionaries);

        statusTv = (TextView) findViewById(R.id.tv_progress_status);
        unzipProgress = (ProgressBar) findViewById(R.id.pb_unzip);
        downloadProgress = (ProgressBar) findViewById(R.id.pb_download);

        ViewUtils.setColorOfProgressBar(unzipProgress, ContextCompat.getColor(this, R.color.theme_primary));
        ViewUtils.setColorOfProgressBar(downloadProgress, ContextCompat.getColor(this, R.color.theme_primary));

        downloadButton = (ImageView) findViewById(R.id.btn_download_wordnet);
        cancelButton = (ImageView) findViewById(R.id.btn_cancel_download_wordnet);
        installLivioButton = (ImageView)findViewById(R.id.btn_install_livio);

        inject();
        setDictionaryFlags();

        View nextButton = findViewById(R.id.btn_next);
        if(!PrefUtils.getTutorialDone(this)){
            nextButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.GONE);
        }
        nextButton.setOnClickListener(this);

        mThread = DownloadResolver.setUpProgress(Constants.WORDNET, downloadProgress, statusTv, this);
    }

    private void inject(){
        DaggerDictionaryComponent.builder().livioModule(new LivioModule())
                .wordnetModule(new WordnetModule()).build().inject(this);
    }

    private void setDictionaryFlags(){
        livioTask = new LivioMeaningsTask().execute();
        wordnetTask = new WordnetMeaningsTask().execute();
    }

    private class LivioMeaningsTask extends AsyncTask<String, Integer, Void> {
        private DictionaryException livioException;
        @Override
        protected Void doInBackground(String... params) {
            try {
                livioDictionary.results(testWord);
            } catch (DictionaryException exception) {
                livioException = exception;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (livioException != null) {
                installLivioButton.setImageResource(R.drawable.ic_play_store);
                installLivioButton.setOnClickListener(DictionariesActivity.this);
            } else {
                installLivioButton.setImageResource(R.drawable.ic_tick);
                installLivioButton.setColorFilter(R.color.green);
            }
        }
    }

    private class WordnetMeaningsTask extends AsyncTask<String, Integer, Void> {
        private DictionaryException wordnetException;
        @Override
        protected Void doInBackground(String... params) {
            try {
                wordnetDictionary.results(testWord);
            } catch (DictionaryException exception) {
                wordnetException = exception;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (wordnetException != null) {
                downloadButton.setImageResource(R.drawable.ic_download);
                downloadButton.setOnClickListener(DictionariesActivity.this);
                cancelButton.setOnClickListener(DictionariesActivity.this);
            }else {
                downloadButton.setImageResource(R.drawable.ic_tick);
                downloadButton.setColorFilter(R.color.green);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromUnzipService();
        livioTask.cancel(true);
        wordnetTask.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        unzipProgress.setVisibility(View.GONE);
        statusTv.setVisibility(View.GONE);

        //Set the user has visited dictionaries screen
        PrefUtils.setDictionariesDone(true, this);
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
            case R.id.btn_install_livio:
                installLivio();
                break;
            case R.id.btn_next:
                next();
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
        statusTv.setVisibility(View.VISIBLE);
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
        statusTv.post(new Runnable() {
            @Override
            public void run() {
                statusTv.setVisibility(View.GONE);
            }
        });
    }

    public void installLivio(){
        String livio = "livio.pack.lang.en_US";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + livio)));
        } catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + livio)));
        }
    }

    public void next(){
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
