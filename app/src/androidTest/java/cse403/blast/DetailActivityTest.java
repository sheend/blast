package cse403.blast;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by graceqiu on 2/17/16.
 */

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    @Rule
    public final ActivityTestRule<DetailActivity> rule = new ActivityTestRule<>(DetailActivity.class, true, false);

    @Test
    public void shouldLaunchDetailPage() {
        Event e = new Event("launchTest", "launchTitle", "launchDesc", "launchLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_layout)).check(matches(isDisplayed()));
    }

    // TODO: test location is displayed correctly
    @Test
    public void shouldDisplayCorrectInfo() {
        Event e = new Event("displayTest", "displayTitle", "displayDesc", "displayLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_title)).check(matches(withText("displayTitle")));
        onView(withId(R.id.detail_desc)).check(matches(withText("what: displayDesc")));
        onView(withId(R.id.detail_time)).toString().equals(new Date(1).toString());
        onView(withId(R.id.create_limit)).toString().equals("100");
    }

    @Test
    public void shouldDisplayLeaveIfAttendee() {
        Event e = new Event("attendeeTest", "attendeeTitle", "attendeeDesc", "attendeeLoc", 100, new Date(1));
        e.addAttendee(new User("Grace", "Grace")); // Grace is hardcoded current user in DetailActivity
        launchActivity(e);
        onView(withId(R.id.detail_button)).check(matches(withText("leave blast :(")));
    }

    @Test
    public void shouldDisplayEditIfOwner() {
        Event e = new Event("ownerTest", "ownerTitle", "ownerDesc", "ownerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).check(matches(withText("edit blast")));
    }

    @Test
    public void strangerButtonDisplay() {
        Event e = new Event("strangerTest", "strangerTitle", "strangerDesc", "strangerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).check(matches(withText("have a blast! :)")));
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
