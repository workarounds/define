package in.workarounds.define.service;

import android.os.AsyncTask;

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
    private String mZipFile;
	private String mRootPath;
	private int progress = 0;
	private int max = 0;
	private UnzipListener mUnzipListener;

	public UnzipTask(UnzipListener unzipListener) {
		this.mUnzipListener = unzipListener;
		mZipFile = FileUtils.getZipFile().getAbsolutePath();
		mRootPath = FileUtils.getRootFilePath();
		dirChecker("");
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
			ZipFile zip = new ZipFile(mZipFile);
			max = zip.size();
			FileInputStream fin = new FileInputStream(mZipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry zipEntry = null;
			while ((zipEntry = zin.getNextEntry()) != null) {
                LogUtils.LOGV(TAG, "Unzipping " + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					dirChecker(zipEntry.getName());
				} else {
					progress++;
					publishProgress(progress);

					FileOutputStream outputStream = new FileOutputStream(mRootPath + File.separator
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
            LogUtils.LOGE(TAG, "unzip erro", e);
		}

		return progress;
	}

	private void dirChecker(String dir) {
		File f = new File(mRootPath + File.separator + dir);

		if (!f.isDirectory()) {
            LogUtils.LOGD(TAG, "Creating file: " + f.getAbsolutePath());
            if(!f.mkdirs()) {
                LogUtils.LOGE(TAG, "Unable to create: " + f.getAbsolutePath());
            }
		}
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		mUnzipListener.onProgressUpdate(this.progress * 100 / max);
	}

	@Override
	protected void onPostExecute(Integer result) {
        LogUtils.LOGD(TAG, "Completed. Total size: " + result);
		mUnzipListener.onFinishUnzip();
	}

    public interface UnzipListener {
        public void onInitUnzip();
        public void onProgressUpdate(long progress);
        public void onFinishUnzip();
    }
}