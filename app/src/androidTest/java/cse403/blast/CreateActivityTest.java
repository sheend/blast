package cse403.blast;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
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

    // TODO(cyndi): before testing, genymotion not working
    @Test
    public void disabledFieldsWhenEdit() {
        launchActivity(false);
        changeNonEditableField(R.id.create_title);
        changeNonEditableField(R.id.create_location);
    }

    @Test
    public void buttonDisplayIfEdit() {
        launchActivity(true);
        onView(withId(R.id.create_submit_button)).check(matches(withText("save blast")));
        onView(withId(R.id.create_cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.create_cancel_button)).check(matches(withText("cancel blast :(")));
    }

    // TODO(cyndi): before testing, genymotion not working
    @Test
    public void cancelButtonGoneIfNotEdit() {
        launchActivity(false);
        onView(withText("Cancel Blast :")).check(doesNotExist());
    }

    @Test
    public void buttonDisplayIfNotEdit() {
        launchActivity(false);
        onView(withId(R.id.create_submit_button)).check(matches(withText("blast it!")));
    }

    // TODO(aixin): before testing, genymotion not working
    @Test
    public void verifyUserInput() {

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

    /**
     * Tries to change the text inside the the given EditText fields
     * @param id integer representation of the field that's trying to be changed
     */
     private void changeNonEditableField(int id) {
         String test = "TEST1_TEST2_TEST3_TEST4";
         onView(withId(id))
                 .perform(ViewActions.click())
                 .perform(typeText(test));
         onView(withText(test)).check(doesNotExist());
     }
}
