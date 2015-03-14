package in.workarounds.define.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import in.workarounds.define.util.FileUtils;

/**
 * Background Async Task to download file
 * */
public class DownloadTask extends AsyncTask<String, String, String> {
	private DownloadListener mDownloadListener;

	public DownloadTask(DownloadListener downloadListener) {
		mDownloadListener = downloadListener;
	}

	/**
	 * Before starting background thread Show Progress Bar Dialog
	 * */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDownloadListener.onInitDownload();
	}

	/**
	 * Downloading file in background thread
	 * */
	@Override
	protected String doInBackground(String... f_url) {
		int count;
		try {
			URL url = new URL(f_url[0]);
			URLConnection conection = url.openConnection();
			conection.connect();
			// this will be useful so that you can show a tipical 0-100%
			// progress bar
			int lenghtOfFile = conection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream(), 8192);

			// Output stream
			OutputStream output = new FileOutputStream(FileUtils.getZipFile());

			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1 && !isCancelled()) {
				total += count;
				// publishing the progress....
				// After this onProgressUpdate will be called
				publishProgress("" + (int) ((total * 100) / lenghtOfFile));

				// writing data to file
				output.write(data, 0, count);
			}

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		}

		return null;
	}

	/**
	 * Updating progress bar
	 * */
	protected void onProgressUpdate(String... progress) {
		// setting progress percentage
		mDownloadListener.onProgressUpdate(Integer.parseInt(progress[0]));
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	@Override
	protected void onPostExecute(String file_url) {
		// dismiss the dialog after the file was downloaded
		mDownloadListener.onFinishDownload();
	}

    public interface DownloadListener {
        public void onInitDownload();
        public void onProgressUpdate(int progress);
        public void onFinishDownload();
    }

}