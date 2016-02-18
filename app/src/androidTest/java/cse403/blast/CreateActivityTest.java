package cse403.blast;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
//import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
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
        Intent createIntent = new Intent();
        createIntent.putExtra("edit", true);
        rule.launchActivity(createIntent);
        onView(withId(R.id.submit_button)).check(matches(withText("Save Blast")));
        onView(withId(R.id.cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_button)).check(matches(withText("Cancel Blast :(")));
    }

    @Test
    public void buttonDisplayIfNotEdit() {
        Intent createIntent = new Intent();
        createIntent.putExtra("edit", false);
        rule.launchActivity(createIntent);
        //onView(withId(R.id.cancel_button)).check(doesNotExist());
        onView(withId(R.id.submit_button)).check(matches(withText("Blast It!")));
    }
}
