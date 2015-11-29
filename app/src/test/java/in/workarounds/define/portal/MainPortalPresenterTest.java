package in.workarounds.define.portal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import in.workarounds.define.base.MeaningPresenter;
import in.workarounds.define.base.NotificationUtils;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Created by manidesto on 29/11/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainPortalPresenterTest {
    @Mock PortalView portalView;
    @Mock NotificationUtils notificationUtils;
    @Mock MeaningPresenter meaningPresenter1;
    @Mock MeaningPresenter meaningPresenter2;

    MainPortalPresenter subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        subject = new MainPortalPresenter(portalView, notificationUtils);
    }

    @Test
    public void testOnCall() throws Exception {
        subject.onCall();

        //verify whether portal is closed on Call
        verify(portalView).hideAndFinish();
        //verify whether the backup notification is shown
        verify(notificationUtils).sendSilentBackupNotification(anyString());
    }

    @Test
    public void testOnClipTextChanged() throws Exception {
        String oneWord = "one";
        String twoWords = "one two";
        String twoWordsWithSpace = "one two ";
        String text = "one two three four";

        //ONE WORD
        subject.onClipTextChanged(oneWord);
        //verify setTextForSelection
        verify(portalView).setTextForSelection(oneWord);
        //verify selectAll
        verify(portalView).selectAll();

        reset(portalView);

        //TWO WORDS
        subject.onClipTextChanged(twoWords);
        verify(portalView).setTextForSelection(twoWords);
        verify(portalView).selectAll();

        reset(portalView);

        //TWO WORDS With Space
        subject.onClipTextChanged(twoWordsWithSpace);
        verify(portalView).setTextForSelection(twoWords);
        verify(portalView).selectAll();

        reset(portalView);

        //FOUR WORDS
        subject.onClipTextChanged(text);
        verify(portalView).setTextForSelection(text);
        verify(portalView, never()).selectAll();
    }

    @Test
    public void testOnWordSelected() throws Exception {
        String selected = "word";

        subject.addMeaningPresenter(meaningPresenter1);
        subject.onWordSelected(selected);
        verify(meaningPresenter1).onWordUpdated(selected);

        subject.addMeaningPresenter(meaningPresenter2);
        verify(meaningPresenter2).onWordUpdated(selected);

        reset(meaningPresenter1);
        reset(meaningPresenter2);

        subject.removeMeaningPresenter(meaningPresenter2);
        String selectedAgain = "different";
        subject.onWordSelected(selectedAgain);
        verify(meaningPresenter1).onWordUpdated(selectedAgain);
        verify(meaningPresenter2, never()).onWordUpdated(anyString());
    }
}