package in.workarounds.define.service;

import android.content.Context;
import android.os.AsyncTask;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import in.workarounds.define.api.Client;
import in.workarounds.define.api.Constants;
import in.workarounds.define.util.FileUtils;

/**
 * Background Async Task to download file
 */
public class DownloadTask extends AsyncTask<Void, Long, Boolean> {
    private DownloadListener mDownloadListener;
    private Context mContext;

    public DownloadTask(DownloadListener downloadListener, Context context) {
        mContext = context;
        mDownloadListener = downloadListener;
    }

    /**
     * Before starting background thread Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDownloadListener.onInitDownload();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected Boolean doInBackground(Void... f_url) {
        OkHttpClient client = Client.getClientInstance(mContext);
        Call call = client.newCall(new Request.Builder().url(Constants.WORDNET_DOWNLOAD_URL).get().build());
        try {
            Response response = call.execute();
            if (response.code() == 200) {
                InputStream inputStream = null;
                OutputStream outputStream = new FileOutputStream(FileUtils.getZipFile());
                try {
                    inputStream = response.body().byteStream();
                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    long target = response.body().contentLength();

                    publishProgress(0L, target);
                    while (true) {
                        int readed = inputStream.read(buff);
                        if (readed == -1) {
                            break;
                        }
                        //write buff
                        outputStream.write(buff, 0, readed);
                        downloaded += readed;
                        publishProgress(downloaded, target);
                        if (isCancelled()) {
                            return false;
                        }
                    }
                    outputStream.flush();
                    return downloaded == target;
                } catch (IOException ignore) {
                    return false;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    outputStream.close();
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updating progress bar
     */

    protected void onProgressUpdate(Long... progress) {
        // setting progress percentage
        mDownloadListener.onProgressUpdate(progress[0]/progress[1]);
    }

    /**
     * After completing background task Dismiss the progress dialog
     * *
     */
    @Override
    protected void onPostExecute(Boolean result) {
        // dismiss the dialog after the file was downloaded
        mDownloadListener.onFinishDownload(result);
    }

    public interface DownloadListener {
        public void onInitDownload();

        public void onProgressUpdate(long progress);

        public void onFinishDownload(boolean result);
    }

}