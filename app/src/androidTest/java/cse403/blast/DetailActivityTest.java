package cse403.blast;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Date;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by graceqiu on 2/17/16.
 *
 * Tests DetailActivity class.
 */

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {
    private User testUser = new User("testId", "testName");

    @Rule
    public final ActivityTestRule<DetailActivity> rule = new ActivityTestRule<>(DetailActivity.class, true, false);

    @Test
    public void shouldLaunchDetailPage() {
        Event e = new Event("launchTest", "launchTitle", "launchDesc", "launchLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayCorrectInfo() {
        Event e = new Event("displayTest", "displayTitle", "displayDesc", "displayLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_title)).check(matches(withText("displayTitle")));
        onView(withId(R.id.detail_desc)).check(matches(withText("displayDesc")));
        onView(withId(R.id.detail_location)).check(matches(withText("displayLoc")));
        onView(withId(R.id.detail_time)).toString().equals(new Date(1).toString());
    }

    @Test
    public void shouldDisplayLeaveIfAttendee() {
        Event e = new Event("attendeeTest", "attendeeTitle", "attendeeDesc", "attendeeLoc", 100, new Date(1));
        e.addAttendee(testUser);
        launchActivity(e);
        onView(withId(R.id.detail_button)).check(matches(withText(R.string.detail_leave)));
    }

    @Test
    public void shouldDisplayEditIfOwner() {
        Event e = new Event(testUser.getFacebookID(), "ownerTitle", "ownerDesc", "ownerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).check(matches(withText(R.string.detail_edit)));
    }

    @Test
    public void strangerButtonDisplay() {
        Event e = new Event("strangerTest", "strangerTitle", "strangerDesc", "strangerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).check(matches(withText(R.string.detail_join)));
    }

    /**
     * Create intent and launch activity with given e
     * @param e event to include in intent when launching activity
     */
    private void launchActivity(Event e) {
        Intent detailIntent = new Intent();
        detailIntent.putExtra("event", e);
        rule.launchActivity(detailIntent);
    }
}
