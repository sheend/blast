package cse403.blast;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;
import org.junit.Test;
import java.util.Date;
import cse403.blast.Model.Event;

/**
 * Created by graceqiu on 2/26/16.
 */
public class DetailActivityTest extends ActivityUnitTestCase<DetailActivity> {
    private static final int TIMEOUT = 2000; // 2000ms

    // Activity of the Target application
    DetailActivity detailActivity;

    public DetailActivityTest() {
        super(DetailActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Starts the create activity of the target application
        Intent detailIntent = new Intent();
        detailIntent.putExtra("event", new Event("junitTest", "junit", "junit", "junit", 1, new Date(1)));
        startActivity(detailIntent, null, null);
        // Getting a reference to the MainActivity of the target application
        detailActivity = (DetailActivity) getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test(timeout = TIMEOUT)
    public void testTitle() {
        Event e = (Event) detailActivity.getIntent().getSerializableExtra("event");
        TextView title = (TextView) detailActivity.findViewById(R.id.detail_title);
        assertEquals(e.getTitle(), title.getText().toString());
    }

    @Test(timeout = TIMEOUT)
    public void testDesc() {
        Event e = (Event) detailActivity.getIntent().getSerializableExtra("event");
        TextView desc = (TextView) detailActivity.findViewById(R.id.detail_desc);
        assertEquals(e.getTitle(), desc.getText().toString());
    }

    @Test(timeout = TIMEOUT)
    public void testLocation() {
        Event e = (Event) detailActivity.getIntent().getSerializableExtra("event");
        TextView loc = (TextView) detailActivity.findViewById(R.id.detail_location);
        assertEquals(e.getTitle(), loc.getText().toString());
    }
}
