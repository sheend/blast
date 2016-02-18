package cse403.blast;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
//import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;

/**
 * Created by michellelee on 2/15/16.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

//    @Rule
//    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

//    @Before
//    public void setUp() {
//        // Make new event called TestEvent
//        onView(withId(R.id.fab)).perform(ViewActions.click());
//        onView(withId(R.id.title)).perform(ViewActions.typeText("TestEvent"));
//        onView(withId(R.id.description)).perform(ViewActions.typeText("TestEvent"));
//        onView(withId(R.id.submit_button)).perform(ViewActions.click());
//    }

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

    // TODO: where is the hamburger??
    @Test
    public void goToDrawerOnClickHamburger() {
    }
    
}
