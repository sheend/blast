package cse403.blast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    private final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent detailIntent = getIntent();

        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(getIntent().getStringExtra("name"));

        TextView time = (TextView) findViewById(R.id.detail_time);
        time.setText("@" + getIntent().getStringExtra("time"));

        TextView desc = (TextView) findViewById(R.id.detail_desc);
        desc.setText(getIntent().getStringExtra("desc"));

        TextView attendees = (TextView) findViewById(R.id.detail_attendees);
        attendees.setText(getIntent().getStringExtra("attendees"));

        Button button = (Button) findViewById(R.id.button);
        // TODO: get current user's status (new viewer, current attendee, or owner)
        // TODO: and display appropriate text and connect to appropriate onclick
        boolean isOwner = false;
        boolean isAttendee = false;
        if (!isOwner) {
            if (!isAttendee) {
                button.setText("Let's Have a Blast! :)");
            } else {
                button.setText("Leave Blast :(");
            }
        } else {
            button.setText("Cancel Blast :(");
        }

    }

}
