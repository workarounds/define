package in.workarounds.define.ui.view.swipeselect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import in.workarounds.define.BuildConfig;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by manidesto on 05/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SelectMovementMethodTest {
    @Mock
    SelectMovementMethod.SelectableView selectableView;

    SelectMovementMethod movementMethod;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(selectableView.getContext()).thenReturn(RuntimeEnvironment.application.getBaseContext());
        movementMethod = new SelectMovementMethod(selectableView);
    }

    @Test
    public void testSelectText() throws Exception {
        String text = "Sample text for testing";

        List<SelectSpan> spanList = getMockSpansFromText(text);
        movementMethod.setSelectSpanList(spanList);

        movementMethod.selectByRange(0, 5);

        assertEquals(spanList.get(3).isSelected(), true);
    }

    private List<SelectSpan> getMockSpansFromText(String text) {
        List<SelectSpan> spanList = new ArrayList<>(text.length());
        BreakIterator iterator = BreakIterator.getCharacterInstance();
        iterator.setText(text);
        int start = 0;
        iterator.first();
        for(int end = iterator.next(); end != BreakIterator.DONE; end = iterator.next()) {
            SelectSpan selectSpan = new SelectSpan(start, start, end);
            spanList.add(selectSpan);
        }
        return spanList;
    }
}
