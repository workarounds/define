package in.workarounds.define.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import in.workarounds.define.R;
import in.workarounds.define.model.WordnetDictionary;
import in.workarounds.define.service.ClipboardService;
import in.workarounds.define.service.DownloadTask;
import in.workarounds.define.service.UnzipTask;
import in.workarounds.define.util.FileUtils;
import in.workarounds.define.util.LogUtils;
import in.workarounds.define.util.NetworkUtils;


public class MainActivity extends ActionBarActivity implements DownloadTask.DownloadListener, UnzipTask.UnzipListener {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);

    private WordnetDictionary dictionary = null;
    private DownloadTask downloadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
     * creates a folder "Recopy" in sd card if not present generates absolute
     * paths and files to /sdcard/Recopy/dict.zip and /sdcard/Recopy/dict/
     */
    private void initFiles() {
        FileUtils.createRootFile();
    }


    @Override
    protected void onStart() {
        startClipboardService();
        super.onStart();
        // The activity is about to become visible.
    }

    @Override
    protected void onResume() {
        startClipboardService();
        super.onResume();
        // The activity has become visible (it is now "resumed").
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be
        // "paused").
    }

    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }

    /**
     * checks if the database is downloaded or not
     * @return
     */
    private boolean isDataAvailable() {
        return FileUtils.getDictFile().exists();
    }

    /**
     * function called on click of "Download Data" button calls
     * checkNetworkStatus and takes appropriate action
     * @param v
     */
    public void downloadClick(View v) {
        if (!this.isDataAvailable()) {
            switch (NetworkUtils.checkNetworkStatus(this)) {
                // using wifi, directly download
                case NetworkUtils.WIFI:
                    downloadTask = new DownloadTask(this);
                    downloadTask.execute(FileUtils.getDictFilePath());
                    break;
                // using 3g, ask user saying 11MB is about to be downloaded
                case NetworkUtils.MOBILE:
                    String title = "Download over Mobile Network?";
                    String body = "The file going to be downloaded is 11MB. Are you sure you want to download it over Mobile Network?";
                    this.showDownloadAlert(title, body);
                    break;
                // no network, ask user if he wants to go to network settings
                case NetworkUtils.NO_NETWORK:
                    // TODO show dialog for network settings
                    break;
                // shouldn't get to this point ever
                default:
                    Log.e("MainActivity",
                            "Something's wrong with checkNetworkStatus()");
            }
        } else {
            // ask if user wants to redownload and proceed accordingly
            String title = "Dictionary Data already present";
            String body = "Do you want to redownload the 11MB data?";
            this.showDownloadAlert(title, body);
        }
    }

    /**
     * Creates an alert dialog with Yes and No
     *
     * @param title
     * @param body
     */
    private void showDownloadAlert(String title, String body) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        downloadTask = new DownloadTask(MainActivity.this);
                        downloadTask.execute(FileUtils.getDictFilePath());
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Set other dialog properties
        builder.setTitle(title).setMessage(body);

        // Create the AlertDialog
        dialog = builder.create();
        dialog.show();
    }

    /**
     * button's function to cancel ongoing download
     *
     * @param v
     */
    public void cancelClick(View v) {
        downloadTask.cancel(true);
        this.onProcessCancelled();
    }

    /**
     * function called when "Find Meaning" is clicked calls
     * dictionary.getMeaning(String wordForm)
     *
     * @param v
     */
    public void meaningClick(View v) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String wordForm = editText.getText().toString();
        if (dictionary != null) {
            dictionary.getMeanings(wordForm);
        }
    }

    /**
     * hides the download button and shows download progress
     */
    @Override
    public void onInitDownload() {
        findViewById(R.id.download_button).setVisibility(View.GONE);

        TextView statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText("Download status");
        statusText.setVisibility(View.VISIBLE);

        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
    }

    /**
     * when the download is cancelled mid way, shows the download button again
     * called in "cancelClick"
     */
    private void onProcessCancelled() {
        findViewById(R.id.download_button).setVisibility(View.VISIBLE);

        TextView statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText("Download status");
        statusText.setVisibility(View.GONE);

        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        findViewById(R.id.cancel_button).setVisibility(View.GONE);
    }

    /**
     * function called on end of download
     */
    @Override
    public void onFinishDownload() {
        new UnzipTask(this).execute();
        TextView statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText("Unzipping status");
        findViewById(R.id.cancel_button).setVisibility(View.GONE);
    }

    /**
     * function to update progressbar both from async download and asyc
     * unzipping
     * @param parseInt
     */
    @Override
    public void onProgressUpdate(int parseInt) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(parseInt);
    }

    /**
     * called when unzipping is finished creates the dictionary object and the
     * correct buttons
     */
    public void onFinishUnzip() {
        TextView statusText = (TextView) findViewById(R.id.status_text);
        statusText.setText("Dictionary in sd card");

        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        if(FileUtils.getZipFile().delete()) {
            LogUtils.LOGD(TAG, "Deleted zip file");
        } else {
            LogUtils.LOGE(TAG, "Unable to delete zip file");
        }
        dictionary = new WordnetDictionary(this);
    }

    /**
     * called before starting unzipping
     */
    public void onInitUnzip() {
        Toast toast = Toast.makeText(this, "starting unzipping",
                Toast.LENGTH_LONG);
        toast.show();
    }

}
