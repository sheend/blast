package cse403.blast;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by michellelee on 2/15/16.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void launchMainScreenWithCorrectTitle() {
        onView(withText("Blast")).check(matches(isDisplayed()));
    }

    @Test
    public void goToCreatePageOnClickPlus() {
        onView(withId(R.id.fab)).perform(ViewActions.click());
        onView(withId(R.id.create_event_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void goToDetailPageOnClickEvent() {
        onView(withId(R.id.main_blast_list_view)).perform(ViewActions.click());
        onView(withId(R.id.detail_layout)).check(matches(isDisplayed()));
    }

    // TODO: test if hamburger clicked, drawer opens. Currently, hamburger cannot be found.
    @Test
    public void goToDrawerOnClickHamburger() {
//        onView(withId(R.id.nav_view)).perform(ViewActions.click());
//        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }

    // TODO: test that an event disappears from main screen once time expires
    @Test
    public void shouldNotShowExpiredEvents() {
    }
}
