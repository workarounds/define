package in.workarounds.define.wordnet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import in.workarounds.define.helper.ContextHelper;
import in.workarounds.define.portal.MeaningsController;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Created by manidesto on 03/12/15.
 */
public class WordnetPresenterTest {
    @Mock
    WordnetDictionary dictionary;

    @Mock
    WordnetMeaningAdapter adapter;

    @Mock
    MeaningsController controller;

    @Mock
    WordnetMeaningPage meaningPage;

    @Mock
    ContextHelper contextHelper;

    WordnetPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new WordnetPresenter(dictionary, adapter, controller, contextHelper);
    }

    @Test
    public void testOnWordUpdated() throws Exception {
        String word = "Some word";
        presenter.onWordUpdated(word);
        verify(dictionary).resultsObservable(word);

        reset(dictionary);

        //on same word updated, dictionary should not be called
        presenter.onWordUpdated(word);
        verify(dictionary, never()).resultsObservable(anyString());
    }
}