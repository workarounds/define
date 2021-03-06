package in.workarounds.define.file.unzip;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import dagger.Lazy;
import in.workarounds.define.R;
import in.workarounds.define.api.Constants;
import in.workarounds.define.file.FileHelper;
import in.workarounds.define.util.FileUtils;
import in.workarounds.define.util.PrefUtils;
import in.workarounds.define.wordnet.WordnetFileHelper;
import timber.log.Timber;

/**
 * Created by madki on 14/05/15.
 */
public class UnzipService extends Service {

    /**
     * constants for messages
     */
    public static final int MSG_UNZIP      = 1;
    public static final int MSG_REGISTER   = 2;
    public static final int MSG_UNREGISTER = 3;

    /**
     * intent key to send the dictionary to unzip
     */
    public static final String INTENT_KEY_DICT_NAME = "intent_key_dict_name";

    /**
     * messenger that is used by activities to communicate to Service
     * activities use this to post messages to service
     */
    private Messenger mMessenger = new Messenger(new IncomingHandler(this));

    /**
     * messenger that is handled by activities
     * use this to post commands to activity
     */
    private Messenger mActivity;

    /**
     * true if service is bound to some activity
     */
    private boolean mBound;
    /**
     * a map of dictionary name and async task handling
     * the unzipping of that dictionary
     */
    private HashMap<String, AsyncTask> mTasks = new HashMap<>();

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private int notificationCount = 0;

    @Inject
    Lazy<WordnetFileHelper> wordnetHelper;

    private UnzipComponent component;

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        component = DaggerUnzipComponent.create();
        component.inject(this);

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);

        mNotificationBuilder.setSmallIcon(R.drawable.ic_notification_icon);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotificationBuilder.setColor(ContextCompat.getColor(this, R.color.theme_primary));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String dictName = intent.getStringExtra(INTENT_KEY_DICT_NAME);
            if(dictName != null) {
                startUnzipTask(dictName);
            } else {
                Timber.e("No dictionary name sent to unzip");
            }
        } else {
            Timber.e("Intent is null !");
        }
        return Service.START_STICKY;
    }

    /**
     * set the activity messenger
     * @param messenger messenger to pass messages to activity
     */
    public void setActivity(Messenger messenger) {
        mActivity = messenger;
        mBound = true;
    }

    /**
     * remove the messenger to activity
     */
    public void unsetActivity() {
        mActivity = null;
        mBound = false;
    }

    /**
     * helper method to send a message to connected activity
     * @param message to be sent to activity
     */
    public void sendToActivity(Message message) {
        if(!mBound) {
            Timber.e("No activity bound to send message");
        } else {
            try {
                mActivity.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * helper method to start the async task
     * @param dictName name of dictionary
     */
    private void startUnzipTask(String dictName) {
        if(mTasks.containsKey(dictName)) {
            Timber.w("Unzipping already in progress. Ignoring unzip command");
        } else {
            UnzipTask asyncTask = new UnzipTask();
            mTasks.put(dictName, asyncTask);
            startTask(asyncTask, dictName);
        }
    }

    /**
     * helper method to start async task in a separate thread
     * @param asyncTask task to be started
     * @param dictName param to be passed
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void startTask(UnzipTask asyncTask, String dictName) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dictName);
        else
            asyncTask.execute(dictName);
    }

    /**
     * method to check if the service can be terminated. If yes, stops the
     * service
     */
    private void checkForTermination() {
        if(!mBound && (mTasks.size()==0)) {
            stopSelf();
        } else {
            if(mBound) {
                Timber.d("An activity is still bound not stopping service");
            }
            if(mTasks.size() != 0) {
                Timber.d("Tasks still in progress not stopping service");
            }
        }
    }

    /**
     * Handler of incoming messages from clients.
     */
    private static class IncomingHandler extends Handler {
        private WeakReference<UnzipService> mService;

        public IncomingHandler(UnzipService unzipService) {
            mService = new WeakReference<UnzipService>(unzipService);
        }

        @Override
        public void handleMessage(Message msg) {
            UnzipService service = mService.get();
            if(service != null) {
                switch (msg.what) {
                    case MSG_REGISTER:
                        service.setActivity((Messenger) msg.obj);
                        break;
                    case MSG_UNZIP:
                        Toast.makeText(service.getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                        Message message = Message.obtain(null, UnzipHandler.MSG_UNZIP_PROGRESS, 20, 0);
                        service.sendToActivity(message);
                        break;
                    case MSG_UNREGISTER:
                        service.unsetActivity();
                        service.checkForTermination();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            } else {
                Timber.e("no service to deliver messages");
            }
        }
    }

    private class UnzipTask extends AsyncTask<String, Float, String> {
        private FileHelper fileHelper;
        private int notificationId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            notificationId = notificationCount + 1;
            notificationCount = notificationCount + 1;

            fileHelper = getFileHelper(params[0]);
            PrefUtils.setUnzipped(params[0], false, getApplicationContext());
            float progress = 0;
            float max = 0;
            File zipFile = fileHelper.zipFile();
            if(zipFile != null) {
                try {
                    ZipFile zip = new ZipFile(zipFile);
                    max = zip.size();
                    FileInputStream fin = new FileInputStream(zipFile);
                    ZipInputStream zin = new ZipInputStream(fin);
                    ZipEntry zipEntry = null;
                    while ((zipEntry = zin.getNextEntry()) != null) {
                        Timber.v("Unzipping " + zipEntry.getName());
                        if (zipEntry.isDirectory()) {
                            FileUtils.assertDir(FileHelper.rootFile() + File.separator
                                    + zipEntry.getName());
                        } else {
                            progress++;
                            publishProgress(progress * 100f / max);

                            FileOutputStream outputStream = new FileOutputStream(FileHelper.rootFile() + File.separator
                                    + zipEntry.getName());

                            byte[] buf = new byte[4096];
                            int r;
                            while ((r = zin.read(buf)) != -1) {
                                outputStream.write(buf, 0, r);
                            }
                            zin.closeEntry();
                            outputStream.close();
                        }
                    }
                    zin.close();
                    zip.close();
                } catch (Exception e) {
                    Timber.e("unzip error", e);
                }
            } else {
                Timber.e("Zip file not found for " + params[0]);
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String dictName) {
            super.onPostExecute(dictName);
            PrefUtils.setUnzipped(dictName, true, getApplicationContext());
            File zipFile = fileHelper.zipFile();
            onProgressUpdate(100f);
            if(zipFile != null) {
                if(zipFile.delete()) {
                    Timber.d("deleted zip file for " + dictName);
                } else {
                    Timber.e("Couldn't delete zip file for " + dictName);
                }
            }
            mTasks.remove(dictName);
            finishNotification(fileHelper, notificationId);
            checkForTermination();
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            updateNotification(fileHelper, notificationId, Math.round(values[0]));
            Message msg = Message.obtain(null, UnzipHandler.MSG_UNZIP_PROGRESS, Math.round(values[0]), 0);
            sendToActivity(msg);
        }
    }

    private void updateNotification(FileHelper fileHelper, int id, int progress) {
        // TODO add pending intent
        mNotificationBuilder.setContentTitle(getResources().getString(R.string.unzip_noti_title));
        mNotificationBuilder.setContentText(fileHelper.downloadFileName());
        mNotificationBuilder.setProgress(100, progress, false);
        mNotifyManager.notify(id, mNotificationBuilder.build());
    }

    private void finishNotification(FileHelper fileHelper, int id) {
        // TODO add pending intent ?
        mNotificationBuilder.setContentTitle(getResources().getString(R.string.unzip_noti_finish_title));
        mNotificationBuilder.setContentText(fileHelper.downloadFileName());
        mNotificationBuilder.setProgress(0, 0, false);
        mNotifyManager.notify(id, mNotificationBuilder.build());
    }

    private FileHelper getFileHelper(String fileName) {
        if(fileName.equals(Constants.WORDNET)) {
            return wordnetHelper.get();
        } else {
            Timber.e("No file helper found for : " + fileName);
            return null;
        }
    }
}
