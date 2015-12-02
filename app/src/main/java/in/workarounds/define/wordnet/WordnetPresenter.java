package in.workarounds.define.wordnet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;

import javax.inject.Inject;

import edu.smu.tspell.wordnet.Synset;
import in.workarounds.define.DefineApp;
import in.workarounds.define.R;
import in.workarounds.define.api.Constants;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.helper.DownloadResolver;
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.ui.activity.DictionariesActivity;
import in.workarounds.typography.TextView;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by madki on 26/09/15.
 */
@PerPortal
public class WordnetPresenter implements MeaningPresenter, Observer<List<Synset>>{
    private static final int LOAD_STATUS = 1;
    private static final int LOAD_PROGRESS = 2;
    private static final int MEANING_LIST = 3;

    private MeaningsController controller;
    private DictionaryException dictionaryException;
    private WordnetDictionary dictionary;
    private WordnetMeaningPage wordnetMeaningPage;
    private String word;
    private WordnetMeaningAdapter adapter;
    private TextView loadStatus;
    private Button downloadButton;
    private ProgressBar loadProgress;
    private RecyclerView meaningList;
    private Subscription subscription;

    @Inject
    public WordnetPresenter(WordnetDictionary dictionary, WordnetMeaningAdapter adapter, MeaningsController controller) {
        this.dictionary = dictionary;
        this.adapter = adapter;
        this.controller = controller;
        controller.addMeaningPresenter(this);
    }

    @Override
    public void addView(View view) {
        this.wordnetMeaningPage = (WordnetMeaningPage) view;
        initViews();
        setInitialViews();
    }

    @Override
    public void dropView() {
        this.wordnetMeaningPage = null;
        if(subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        dropViews();
    }

    @Override
    public void onWordUpdated(String word) {
        Timber.d("Word updated : " + word);
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = Observable.just(word)
                .filter(w -> w != null && !w.equals(WordnetPresenter.this.word))
                .doOnNext(w -> {
                    showProgress();
                    updateWordOnPage(w);
                })
                .observeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                .flatMap(this::getResults)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    private Observable<List<Synset>> getResults(final String w) {
        return Observable.create(new Observable.OnSubscribe<List<Synset>>() {
            @Override
            public void call(Subscriber<? super List<Synset>> subscriber) {
                try {
                    List<Synset> result = dictionary.results(w);
                    if(!subscriber.isUnsubscribed()){
                        subscriber.onNext(result);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private void updateWordOnPage(String word) {
        this.word = word;
        if (wordnetMeaningPage != null) {
            wordnetMeaningPage.title(word);
        }
    }

    public DictionaryException getDictionaryException() {
        return dictionaryException;
    }

    public void setDictionaryException(DictionaryException dictionaryException) {
        this.dictionaryException = dictionaryException;
    }

    public String word() {
        return word;
    }

    public WordnetMeaningAdapter adapter() {
        return adapter;
    }

    private void onResultsUpdated(List<Synset> results) {
        adapter.update(results);
        if(wordnetMeaningPage != null) {
            downloadButton.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            if (results != null && results.size() != 0) {
                showList();
            } else {
                showStatus("Sorry, no results found.");
            }
        }
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        DictionaryException exception;
        if(e instanceof DictionaryException) {
            exception = (DictionaryException) e;
        } else {
            //noinspection ThrowableInstanceNeverThrown
            exception = new DictionaryException(
                    DictionaryException.UNKNOWN,
                    "Sorry, something went wrong"
            );
        }
        setDictionaryException(exception);
        showException();
    }

    @Override
    public void onNext(List<Synset> synsets) {
        onResultsUpdated(synsets);
    }

    private void initViews() {
        loadStatus = (TextView) wordnetMeaningPage.findViewById(R.id.tv_load_status);
        downloadButton = (Button) wordnetMeaningPage.findViewById(R.id.btn_download_wordnet);
        loadProgress = (ProgressBar) wordnetMeaningPage.findViewById(R.id.pb_load_progress);
        meaningList = (RecyclerView) wordnetMeaningPage.findViewById(R.id.rv_meaning_list);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Context context = view.getContext();
                    Intent downloadIntent = new Intent(context, DictionariesActivity.class);
                    context.startActivity(downloadIntent);
                    controller.onDownloadClicked();
                }else {
                    DownloadResolver
                            .startDownload(Constants.WORDNET, DefineApp.getContext());
                }
            }
        });
    }

    private void setInitialViews() {
        if (TextUtils.isEmpty(word)) {
            showStatus("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if (adapter != null && adapter.getItemCount() != 0) {
            adapter.notifyDataSetChanged();
            showList();
        } else if (getDictionaryException() != null) {
            showException();
        } else if(subscription != null && !subscription.isUnsubscribed()){
            showProgress();
        } else {
            showStatus("Sorry, no results found");
        }
    }

    private void dropViews() {
        loadStatus = null;
        downloadButton = null;
        loadProgress = null;
        meaningList = null;
    }

    private void showStatus(String status) {
        loadStatus.setText(status);
        showView(LOAD_STATUS);
    }

    private void showException() {
        if(wordnetMeaningPage != null) {
            showStatus(getDictionaryException().getMessage());
            if(getDictionaryException().getType() == DictionaryException.DICTIONARY_NOT_FOUND) {
                downloadButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showProgress() {
        showView(LOAD_PROGRESS);
    }

    private void showList() {
        showView(MEANING_LIST);
    }

    private void showView(@ViewEnum int view) {
        switch (view) {
            case LOAD_STATUS:
                changeViewVisibilities(true, false, false);
                break;
            case LOAD_PROGRESS:
                changeViewVisibilities(false, true, false);
                break;
            case MEANING_LIST:
                changeViewVisibilities(false, false, true);
                break;
        }
    }

    private void changeViewVisibilities(boolean status, boolean progress, boolean list) {
        if (loadStatus != null) {
            loadStatus.setVisibility(status ? View.VISIBLE : View.GONE);
        }
        if (loadProgress != null) {
            loadProgress.setVisibility(progress ? View.VISIBLE : View.GONE);
        }
        if (meaningList != null) {
            meaningList.setVisibility(list ? View.VISIBLE : View.GONE);
        }
    }

    @IntDef({LOAD_PROGRESS, LOAD_STATUS, MEANING_LIST})
    private @interface ViewEnum {
    }
}
