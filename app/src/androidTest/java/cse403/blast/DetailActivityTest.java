package cse403.blast;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by graceqiu on 2/17/16.
 */

@RunWith(AndroidJUnit4.class)
public class DetailActivityTest {

    @Rule
    public final ActivityTestRule<DetailActivity> rule = new ActivityTestRule<>(DetailActivity.class);

    /*
    // TODO: make fake intent to ensure components of page match the event given
    @Test
    public void launchDetailPage() {
        onView(withId(R.id.detail_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void titleCorrect() {
        Intent detailIntent = new Intent();
        rule.launchActivity(detailIntent);
        onView(withId(R.id.detail_title)).check(matches(withText("testTitle")));
        onView(withId(R.id.detail_desc)).check(matches(withText("testDesc")));
        onView(withId(R.id.detail_location)).check(matches(withText("testLocation")));
        onView(withId(R.id.detail_time)).check(matches(withText("testTime")));
    }

    // TODO: when given user who is attendee, show "Leave Blast :("
    @Test
    public void attendeeButtonDisplay() {

    }

    // TODO: when given user who is owner, show "Edit Blast"
    @Test
    public void ownerButtonDisplay() {

    }

    // TODO: when given user who is neither, show "Have a Blast! :)"
    @Test
    public void strangerButtonDisplay() {

    }
    */
}
