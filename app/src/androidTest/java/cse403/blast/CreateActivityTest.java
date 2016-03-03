package cse403.blast;

import android.content.Intent;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.widget.EditText;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

/**
 * Created by graceqiu on 2/17/16.
 */
@RunWith(AndroidJUnit4.class)
public class CreateActivityTest {
    private User testUser = new User("testId", "testName");

    @Rule
    public final ActivityTestRule<CreateEventActivity> rule = new ActivityTestRule<>(CreateEventActivity.class);

    @Test
    public void launchCreatePage() {
        //onView(withId(R.id.create_event_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void disabledFieldsWhenEdit() {
        launchActivity(false, null);
        changeNonEditableField(R.id.create_title,
                onView(withId(R.id.create_title)).toString());
        changeNonEditableField(R.id.create_location,
                onView(withId(R.id.create_location)).toString());
    }

    @Test
    public void buttonDisplayIfEdit() {
        launchActivity(true, new Event(testUser.getFacebookID(), "title", "desc", "loc", 100, new Date(1)));
        onView(withId(R.id.create_submit_button)).check(matches(withText(R.string.create_save_button)));
        onView(withId(R.id.create_cancel_button)).check(matches(isDisplayed()));
        onView(withId(R.id.create_cancel_button)).check(matches(withText(R.string.create_cancel_button)));
    }

    @Test
    public void cancelButtonGoneIfNotEdit() {
        launchActivity(false, null);
        onView(withText(R.string.create_cancel_button)).check(doesNotExist());
    }

    @Test
    public void buttonDisplayIfNotEdit() {
        launchActivity(false, null);
        onView(withId(R.id.create_submit_button)).check(matches(withText(R.string.create_blast_button)));
    }

    /*
    // TODO: test that verification of user input during creation works. Currently, typeText() is buggy.
    @Test
    public void verifyCreatorInput() {
        onView(withId(R.id.create_title)).perform( ViewActions.click(), typeText("test"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.create_description)).perform(ViewActions.click(), typeText("description"));
        onView(withId(R.id.create_date)).perform(ViewActions.click(), typeText("date"));
        onView(withId(R.id.create_time)).perform(ViewActions.click(), typeText("time"));
        onView(withId(R.id.create_location)).perform(ViewActions.click(), typeText("location"));
        onView(withId(R.id.create_limit)).perform(ViewActions.click(), typeText("limit"));
        onView(withId(R.id.create_submit_button)).perform(ViewActions.click()).check(matches(withId(R.id.main_blast_list_view)));
    }

    // TODO: test that verification of user input during editing works.
    public void verifyEditorInput() {

    }

    // TODO: test that invalid user input during creation/editing leads to UI message
    public void shouldShowErrorOnInvalidUserInput() {

    }

    // TODO: test that if an event is updated, notifications are sent
    public void shouldNotifyAttendeesIfEventUpdated() {

    }
    */

    /*// TODO: drawer won't open
    @Test
    public void shouldPopulateDrawerCreatedListIfCreated() {
        launchActivity(false, null);
        // Create new event
        onView(withId(R.id.create_title)).perform( ViewActions.click(), typeText("test"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.create_description)).perform(ViewActions.click(), typeText("description"));
        onView(withId(R.id.create_date)).perform(ViewActions.click(), typeText("date"));
        onView(withId(R.id.create_time)).perform(ViewActions.click(), typeText("time"));
        onView(withId(R.id.create_location)).perform(ViewActions.click(), typeText("location"));
        onView(withId(R.id.create_limit)).perform(ViewActions.click(), typeText("limit"));
        onView(withId(R.id.create_submit_button)).perform(ViewActions.click());
        // Open drawer
        DrawerLayout drawer = (DrawerLayout) rule.getActivity().findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
        onData(anything()).inAdapterView(withId(R.id.created_list)).atPosition(0).check(matches(withText("test")));
    }

    // TODO: drawer won't open
    @Test
    public void shouldUpdateDrawerCreatedListIfCanceling() {
        Event e = new Event(testUser.getFacebookID(), "test", "desc", "loc", 100, new Date(1));
        launchActivity(true, e);
        onView(withId(R.id.create_cancel_button)).perform(ViewActions.click());
        // Open drawer
        onView(withId(R.id.nav_view)).perform(ViewActions.click());
        onData(anything()).inAdapterView(withId(R.id.created_list)).atPosition(0).check(matches(not(withText("test"))));
    }
    */

    /**
     * Create intent and launch activity with given boolean edit
     * @param edit boolean to include in intent when launching activity
    */
    private void launchActivity(boolean edit, Event e) {
        Intent createIntent = new Intent();
        createIntent.putExtra("edit", edit);
        if (edit) {
            createIntent.putExtra("event", e);
        }
        rule.launchActivity(createIntent);
    }

    /**
     * Tries to change the text inside the the given EditText fields
     * @param id integer representation of the field that's trying to be changed
    */
     private void changeNonEditableField(int id, String s) {
         String test = "TEST1_TEST2_TEST3_TEST4";
         onView(withId(id))
                 .perform(ViewActions.click())
                 .perform(typeText(test));
         onView(withId(id)).toString().equals(matches(withText(s)));
     }
}
