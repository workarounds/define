package in.workarounds.define.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import in.workarounds.define.util.FileUtils;
import in.workarounds.define.util.LogUtils;

public class UnzipTask extends AsyncTask<Void, Integer, Integer> {
    private static final String TAG = LogUtils.makeLogTag(UnzipTask.class);
    private String _zipFile;
	private String _location;
	private int per = 0;
	private int max = 0;
	private UnzipListener mUnzipListener;

	public UnzipTask(UnzipListener unzipListener) {
		this.mUnzipListener = unzipListener;
		_zipFile = FileUtils.getZipFile().getAbsolutePath();
		_location = FileUtils.getRootFilePath();
		_dirChecker("");
	}

	/**
	 * Before starting background thread Show Progress Bar Dialog
	 * */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mUnzipListener.onInitUnzip();
	}

	@Override
	protected Integer doInBackground(Void... param) {
		try {
			ZipFile zip = new ZipFile(_zipFile);
			max = zip.size();
			FileInputStream fin = new FileInputStream(_zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {

				Log.v("Decompress", "Unzipping " + ze.getName());
				if (ze.isDirectory()) {
					_dirChecker(ze.getName());
				} else {
					// Here I am doing the update of my progress bar

					per++;
					publishProgress(per);

					FileOutputStream fout = new FileOutputStream(_location
							+ ze.getName());

					byte[] buf = new byte[4096];
					int r;
					while ((r = zin.read(buf)) != -1) {
						fout.write(buf, 0, r);
					}
					zin.closeEntry();
					fout.close();
				}
			}
			zin.close();
			zip.close();
		} catch (Exception e) {
			Log.e("Decompress", "unzip", e);
		}

		return per;
	}

	private void _dirChecker(String dir) {
		File f = new File(_location + dir);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		mUnzipListener.onProgressUpdate(per * 100 / max);
	}

	@Override
	protected void onPostExecute(Integer result) {
        LogUtils.LOGD(TAG, "Completed. Total size: " + result);
		mUnzipListener.onFinishUnzip();
	}

    public interface UnzipListener {
        public void onInitUnzip();
        public void onProgressUpdate(int progress);
        public void onFinishUnzip();
    }
}