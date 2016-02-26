package cse403.blast;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

/**
 * Created by graceqiu on 2/26/16.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
    // Activity of the Target application
    MainActivity mainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Starts the create activity of the target application
        Intent createIntent = new Intent();
        startActivity(new Intent(getInstrumentation().getTargetContext(), CreateEventActivity.class), null, null);
        // Getting a reference to the MainActivity of the target application
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
