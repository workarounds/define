package in.workarounds.define.wordnet;

import android.os.Build;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.smu.tspell.wordnet.Synset;
import in.workarounds.define.BuildConfig;
import in.workarounds.define.RxAndroidHook;
import in.workarounds.define.api.Constants;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.MeaningsController;
import rx.Observable;
import rx.android.plugins.RxAndroidPlugins;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by manidesto on 03/12/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class WordnetPresenterTest {
    @Mock
    WordnetDictionary dictionary;

    @Mock
    MeaningsController controller;

    @Mock
    WordnetMeaningPage meaningPage;

    @Mock
    ContextHelper contextHelper;

    RxAndroidHook schedulerHook = new RxAndroidHook(Schedulers.immediate());

    WordnetPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RxAndroidPlugins.getInstance().registerSchedulersHook(schedulerHook);
        presenter = new WordnetPresenter(dictionary, controller, contextHelper);
    }

    @Test
    public void testAddAndDropView() throws Exception {
        String word = "Some word";
        String someOtherWord = "Some other word";
        presenter.onWordUpdated(word);
        presenter.addView(meaningPage);
        verify(meaningPage).title(word);

        reset(meaningPage);

        presenter.dropView();
        presenter.onWordUpdated(someOtherWord);
        verify(meaningPage, never()).title(anyString());
    }

    @Test
    public void testResultsUpdated() throws Exception {
        String word = "Some word";
        Synset synset = mock(Synset.class);
        List<Synset> results = Arrays.asList(synset, synset, synset);
        when(dictionary.resultsObservable(anyString())).thenReturn(Observable.just(results));

        presenter.addView(meaningPage);
        presenter.onWordUpdated(word);
        verify(meaningPage).meanings(results);
    }

    @Test
    public void testOnWordUpdated() throws Exception {
        String word = "Some word";
        presenter.addView(meaningPage);
        presenter.onWordUpdated(word);
        verify(meaningPage).title(word);
        verify(meaningPage).meaningsLoading();
        verify(dictionary).resultsObservable(word);

        reset(meaningPage);
        reset(dictionary);

        //on same word updated, dictionary should not be called
        presenter.onWordUpdated(word);
        verify(meaningPage, never()).title(anyString());
        verify(meaningPage, never()).meaningsLoading();
        verify(dictionary, never()).resultsObservable(anyString());
    }

    @Test
    public void testErrorHandling() throws Exception {
        String error = "Network error";
        String word = "Some word";
        String someOtherWord = "Some other word";
        //noinspection ThrowableInstanceNeverThrown
        DictionaryException e = new DictionaryException(
                DictionaryException.NETWORK_ERROR, error);

        //test error
        when(dictionary.resultsObservable(anyString())).thenReturn(Observable.error(e));
        presenter.addView(meaningPage);
        presenter.onWordUpdated(word);
        verify(meaningPage).error(error);

        reset(meaningPage);

        //test empty result
        when(dictionary.resultsObservable(anyString())).thenReturn(Observable.just(new ArrayList<>()));
        presenter.onWordUpdated(someOtherWord);
        verify(meaningPage).error(anyString());
    }

    @Test
    public void testDownloadClicked() throws Exception {
        presenter.onDownloadClicked();
        verify(contextHelper).startDownload(Constants.WORDNET);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verify(controller).onDownloadClicked();
        } else {
            verify(controller, never()).onDownloadClicked();
        }
    }

    @After
    public void tearDown() throws Exception {
        RxAndroidPlugins.getInstance().reset();
    }
}