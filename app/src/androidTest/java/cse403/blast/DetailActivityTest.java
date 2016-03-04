package cse403.blast;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
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
import static org.hamcrest.Matchers.anything;
// TODO: import static android.support.test.espresso.contrib.DrawerActions.openDrawer;

/**
 * Created by graceqiu on 2/17/16.
 *
 * Tests DetailActivity class. User should be able to join events not currently attending,
 * should be able to leave events they are currently attending, and should be able to
 * edit an event if they are the owner. The button displayed on the activity should change
 * depending on the user's relationship to the event. Furthermore, the left drawer should update
 * when a user attends or leaves an event.
 */

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {
    private User testUser = new User("testId", "test name");

    @Rule
    public final ActivityTestRule<DetailActivity> rule = new ActivityTestRule<>(DetailActivity.class, true, false);

    @Test
    public void shouldLaunchDetailPage() {
        Event e = new Event("launch test", "launchTitle", "launchDesc", "launchLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldDisplayCorrectInfo() {
        Event e = new Event("display test", "displayTitle", "displayDesc", "displayLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_title)).check(matches(withText("displayTitle")));
        onView(withId(R.id.detail_desc)).check(matches(withText("displayDesc")));
        onView(withId(R.id.detail_location)).check(matches(withText("displayLoc")));
        onView(withId(R.id.detail_time)).toString().equals(new Date(1).toString());
    }

    @Test
    public void shouldDisplayLeaveIfAttendee() {
        Event e = new Event("attendee test", "attendeeTitle", "attendeeDesc", "attendeeLoc", 100, new Date(1));
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
    public void shouldDisplayJoinIfStranger() {
        Event e = new Event("stranger test", "strangerTitle", "strangerDesc", "strangerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).check(matches(withText(R.string.detail_join)));
    }

    /*
    // TODO: drawer won't open
    @Test
    public void shouldPopulateDrawerAttendingListIfAttending() {
        Event e = new Event("strangerTest", "strangerTitle", "strangerDesc", "strangerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).perform(ViewActions.click());
        //onView(withContentDescription("Open navigation")).perform(ViewActions.click());
        DrawerLayout drawer = (DrawerLayout) rule.getActivity().findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
        onData(anything()).inAdapterView(withId(R.id.attending_list)).atPosition(0).check(matches(withText("strangerTitle")));
    }

    // TODO: drawer won't open
    @Test
    public void shouldUpdateDrawerAttendingListIfLeaving() {
        Event e = new Event("strangerTest", "strangerTitle", "strangerDesc", "strangerLoc", 100, new Date(1));
        e.addAttendee(testUser);
        launchActivity(e);
        onView(withId(R.id.detail_button)).perform(ViewActions.click());
        //onView(withContentDescription("Open navigation")).perform(ViewActions.click());
        onView(withId(R.id.nav_view)).perform(ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.attending_list)).atPosition(0).check(matches(withText("strangerTitle")));
    }
    */

    @Test
    public void editClickToEditableCreateEventActivityPage() {
        Event e = new Event(testUser.getFacebookID(), "ownerTitle", "ownerDesc", "ownerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).perform(ViewActions.click());
        onView(withId(R.id.create_event_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void joinClickToMainActivityPage() {
        Event e = new Event("strangerTest", "strangerTitle", "strangerDesc", "strangerLoc", 100, new Date(1));
        launchActivity(e);
        onView(withId(R.id.detail_button)).perform(ViewActions.click());
        onView(withId(R.id.main_blast_list_view)).check(matches(isDisplayed()));
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
