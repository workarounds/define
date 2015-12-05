package in.workarounds.define.urban;

import android.text.TextUtils;
import android.view.View;

import javax.inject.Inject;

import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.portal.PerPortal;
import in.workarounds.define.util.AndroidSchedulersUtil;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import timber.log.Timber;

/**
 * Created by madki on 13/10/15.
 */
@PerPortal
public class UrbanPresenter implements MeaningPresenter, Observer<UrbanResult> {
    private DictionaryException dictionaryException;
    private UrbanDictionary dictionary;
    private UrbanMeaningPage urbanMeaningPage;
    private String word;
    private UrbanMeaningAdapter adapter;
    private Subscription subscription;

    @Inject
    public UrbanPresenter(UrbanDictionary dictionary, UrbanMeaningAdapter adapter, MeaningsController controller) {
        this.dictionary = dictionary;
        this.adapter = adapter;
        controller.addMeaningPresenter(this);
    }

    @Override
    public void addView(View view) {
        this.urbanMeaningPage = (UrbanMeaningPage) view;
        initMeaningPage();
    }

    @Override
    public void dropView() {
        this.urbanMeaningPage = null;
        if(subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

    @Override
    public void onWordUpdated(String word) {
        Timber.d("Word updated : %s", word);
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = Observable.just(word)
                .filter(w -> w != null && !w.equals(UrbanPresenter.this.word))
                .doOnNext(w -> {
                    onMeaningsLoading();
                    updateWordOnPage(w);
                })
                .flatMap(dictionary::resultsObservable)
                .observeOn(AndroidSchedulersUtil.mainThread())
                .subscribe(this);
    }

    private void updateWordOnPage(String word) {
        this.word = word;
        if (urbanMeaningPage != null) {
            urbanMeaningPage.title(word);
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

    public UrbanMeaningAdapter adapter() {
        return adapter;
    }

    private void onResultsUpdated(UrbanResult results) {
        adapter.update(results);
        if(urbanMeaningPage != null) {
            adapter.notifyDataSetChanged();
            if (results != null && results.getMeanings().size() != 0) {
                urbanMeaningPage.meaningsLoaded();
            } else {
                urbanMeaningPage.error("Sorry, no results found.");
            }
        }
    }

    @Override
    public void onCompleted() {
        setDictionaryException(null);
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
    public void onNext(UrbanResult urbanResult) {
        onResultsUpdated(urbanResult);
    }

    private void initMeaningPage() {
        urbanMeaningPage.title(word);
        urbanMeaningPage.setAdapter(adapter);
        if (TextUtils.isEmpty(word)) {
            urbanMeaningPage.error("Please select a word to define. Tap on a word to select one. Or swipe to select multiple words.");
        } else if (adapter != null && adapter.getItemCount() != 0) {
            urbanMeaningPage.meaningsLoaded();
        } else if (getDictionaryException() != null) {
            showException();
        } else if(subscription != null && !subscription.isUnsubscribed()){
            urbanMeaningPage.meaningsLoading();
        } else{
            urbanMeaningPage.error("Sorry, no results found");
        }
    }

    private void showException() {
        if(urbanMeaningPage != null) {
            urbanMeaningPage.error(getDictionaryException().getMessage());
        }
    }

    private void onMeaningsLoading() {
        if(urbanMeaningPage != null) {
            urbanMeaningPage.meaningsLoading();
        }
    }
}
