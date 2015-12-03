package in.workarounds.define.ui.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

/**
 * Created by manidesto on 03/12/15.
 */
@RunWith(AndroidJUnit4.class)
public class TutorialActivityTest {
    @Rule
    public ActivityTestRule<TutorialActivity> activityTestRule =
            new ActivityTestRule<>(TutorialActivity.class);

    @Test
    public void testCopyButton() throws Exception {
        String copy = "copy";
        onView(withText(equalToIgnoringCase(copy))).check(matches(isDisplayed()));
    }
}