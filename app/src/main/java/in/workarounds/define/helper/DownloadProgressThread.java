package in.workarounds.define.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.PrefUtils;

/**
 * Created by madki on 21/05/15.
 */
public class DownloadProgressThread extends Thread {
    private static final String TAG = LogUtils.makeLogTag(DownloadProgressThread.class);
    private boolean mRunning = false;
    private WeakReference<ProgressBar> mProgressBar;
    private WeakReference<TextView> mTextView;
    private long mDownloadReference;
    private DownloadManager mDownloadManager;

    public DownloadProgressThread(String id, ProgressBar progressBar, TextView statusView, Context context) {
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadReference = PrefUtils.getDownloadId(id, context);
        mProgressBar = new WeakReference<ProgressBar>(progressBar);
        mTextView = new WeakReference<TextView>(statusView);
    }

    @Override
    public void run() {

        mRunning = true;
        boolean downloading = true;

        final ProgressBar progressBar = mProgressBar.get();
        final TextView textView = mTextView.get();

        while (mRunning && downloading && mDownloadReference != 0) {

            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(mDownloadReference);

            final Cursor cursor = mDownloadManager.query(q);
            if(cursor.moveToFirst()) {

                int bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false;
                }

                final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                if (progressBar != null) {
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            if(progressBar.getVisibility() != View.VISIBLE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
                            progressBar.setProgress(dl_progress);
                        }
                    });
                }

                if (textView != null) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(getStatusMessage(cursor));
                        }
                    });
                }
            } else {
                if(progressBar != null) {
                    progressBar.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                close();
                LogUtils.LOGE(TAG, "No download with that query");
            }

            cursor.close();
        }
    }

    public void close() {
        mRunning = false;
    }

    private String getStatusMessage(Cursor downloadQuery) {
        String msg = "";

        switch (downloadQuery.getInt(downloadQuery.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";
                break;

            default:
                msg = "Download status unknown";
                break;
        }

        return (msg);
    }

}
