package pc_magas.vodafone_fu_h300s;

import androidx.test.rule.ActivityTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import pc_magas.vodafone_fu_h300s.screens.MainScreen;
import tools.fastlane.screengrab.FalconScreenshotStrategy;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;

@RunWith(JUnit4.class)
public class TakeScreenshots {
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainScreen> activityRule = new ActivityTestRule<>(MainScreen.class);

    @Test
    public void testTakeScreenshot() {
        Screengrab.screenshot("mainScreen");

        // Your custom onView...
//        onView(withId(R.id.fab)).perform(click());

    }
}