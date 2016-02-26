package cse403.blast;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityTestCase;
import android.test.ActivityUnitTestCase;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by graceqiu on 2/26/16.
 */

public class CreateActivityTest extends ActivityUnitTestCase<CreateEventActivity> {

    // Activity of the Target application
    CreateEventActivity createActivity;

    public CreateActivityTest() {
        super(CreateEventActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Starts the create activity of the target application
        Intent createIntent = new Intent();
        startActivity(new Intent(getInstrumentation().getTargetContext(), CreateEventActivity.class), null, null);
        // Getting a reference to the MainActivity of the target application
        createActivity = (CreateEventActivity) getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}