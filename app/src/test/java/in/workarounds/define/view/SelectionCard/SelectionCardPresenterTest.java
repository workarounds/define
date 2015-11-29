package in.workarounds.define.view.SelectionCard;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Created by manidesto on 30/11/15.
 */
public class SelectionCardPresenterTest {
    @Mock SelectionCardListener listener;
    @Mock SelectionCardView selectionCardView;
    SelectionCardPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new SelectionCardPresenter(listener);
    }

    @Test
    public void testOnWordSelected() throws Exception {
        String word = "something";
        presenter.onWordSelected(word);
        verify(listener).onWordSelected(word);
    }

    @Test
    public void testOnClipTextChanged() throws Exception {
        presenter.addView(selectionCardView);

        String oneWord = "one";
        String twoWords = "one two";
        String twoWordsWithSpace = "one two ";
        String text = "one two three four";

        //ONE WORD
        presenter.onClipTextChanged(oneWord);
        //verify setTextForSelection
        verify(selectionCardView).setTextForSelection(oneWord);
        //verify selectAll
        verify(selectionCardView).selectAll();

        reset(selectionCardView);

        //TWO WORDS
        presenter.onClipTextChanged(twoWords);
        verify(selectionCardView).setTextForSelection(twoWords);
        verify(selectionCardView).selectAll();

        reset(selectionCardView);

        //TWO WORDS With Space
        presenter.onClipTextChanged(twoWordsWithSpace);
        verify(selectionCardView).setTextForSelection(twoWords);
        verify(selectionCardView).selectAll();

        reset(selectionCardView);

        //FOUR WORDS
        presenter.onClipTextChanged(text);
        verify(selectionCardView).setTextForSelection(text);
        verify(selectionCardView, never()).selectAll();
    }
}