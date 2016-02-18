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
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by graceqiu on 2/17/16.
 */

@RunWith(AndroidJUnit4.class)
public class CreateActivityTest {

    @Rule
    public final ActivityTestRule<CreateEventActivity> rule = new ActivityTestRule<>(CreateEventActivity.class);

    @Test
    public void launchCreatePage() {
        onView(withId(R.id.create_event_layout)).check(matches(isDisplayed()));
    }

    // TODO: make sure specific text fields are disabled
    @Test
    public void buttonDisplayIfEdit() {
        launchActivity(true);
        onView(withId(R.id.create_submit_button)).check(matches(withText("Save Blast")));
        onView(withId(R.id.create_cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.create_cancel_button)).check(matches(withText("Cancel Blast :(")));
    }

    // TODO: check that cancel button is GONE
    @Test
    public void buttonDisplayIfNotEdit() {
        launchActivity(false);
        onView(withId(R.id.create_submit_button)).check(matches(withText("Blast It!")));
    }

    /**
     * Create intent and launch activity with given boolean edit
     * @param edit boolean to include in intent when launching activity
     */
    private void launchActivity(boolean edit) {
        Intent createIntent = new Intent();
        createIntent.putExtra("edit", edit);
        rule.launchActivity(createIntent);
    }
}
