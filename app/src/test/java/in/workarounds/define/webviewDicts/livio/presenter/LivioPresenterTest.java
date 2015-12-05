package in.workarounds.define.webviewDicts.livio.presenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import in.workarounds.define.BuildConfig;
import in.workarounds.define.RxAndroidHook;
import in.workarounds.define.base.DictionaryException;
import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.MeaningsController;
import in.workarounds.define.webviewDicts.livio.LivioDictionary;
import in.workarounds.define.webviewDicts.livio.LivioMeaningPage;
import rx.Observable;
import rx.android.plugins.RxAndroidPlugins;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by manidesto on 06/12/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class LivioPresenterTest {
    @Mock
    LivioDictionary dictionary;

    @Mock
    MeaningsController controller;

    @Mock
    LivioMeaningPage meaningPage;

    @Mock
    ContextHelper contextHelper;

    RxAndroidHook schedulerHook = new RxAndroidHook(Schedulers.immediate());

    LivioEnglishPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        RxAndroidPlugins.getInstance().registerSchedulersHook(schedulerHook);
        presenter = new LivioEnglishPresenter(dictionary, controller, contextHelper);
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
        String result = "<html></html>";
        when(dictionary.resultsObservable(word, presenter.getPackageName())).thenReturn(Observable.just(result));

        presenter.addView(meaningPage);
        presenter.onWordUpdated(word);
        verify(meaningPage).updateMeanings(result);
    }

    @Test
    public void testOnWordUpdated() throws Exception {
        String word = "Some word";
        presenter.addView(meaningPage);
        presenter.onWordUpdated(word);
        verify(meaningPage).title(word);
        verify(meaningPage).meaningsLoading();
        verify(dictionary).resultsObservable(word, presenter.getPackageName());

        reset(meaningPage);
        reset(dictionary);

        //on same word updated, dictionary should not be called
        presenter.onWordUpdated(word);
        verify(meaningPage, never()).title(anyString());
        verify(meaningPage, never()).meaningsLoading();
        verify(dictionary, never()).resultsObservable(word, presenter.getPackageName());
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
        when(dictionary.resultsObservable(
                word,
                presenter.getPackageName()
        )).thenReturn(Observable.error(e));
        presenter.addView(meaningPage);
        presenter.onWordUpdated(word);
        verify(meaningPage).error(error);

        reset(meaningPage);

        //test empty result
        when(dictionary.resultsObservable(
                someOtherWord,
                presenter.getPackageName()
        )).thenReturn(Observable.just(""));
        presenter.onWordUpdated(someOtherWord);
        verify(meaningPage).error(anyString());
    }

    @After
    public void tearDown() throws Exception {
        RxAndroidPlugins.getInstance().reset();
    }
}