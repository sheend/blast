package cse403.blast.Data;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cse403.blast.Model.Event;
import cse403.blast.R;

/**
 * Created by Sheen on 2/18/16.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Event event = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_event_layout, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.event_title);
        TextView subtitle = (TextView) convertView.findViewById(R.id.event_subtitle);

        // Populate the data into the template view using the data object
        title.setText(event.getTitle());
        subtitle.setText(event.getLocation() + " @ " + event.getEventTime());
        // Return the completed view to render on screen

        ImageView eventImage = (ImageView) convertView.findViewById(R.id.event_category_image);
        //For adding background images
        //int id = getContext().getResources().getIdentifier("cse403.blast:drawable/" + "ic_menu_share", null, null);
        //eventImage.setImageResource(id);

        /*Random r = new Random();

        int color = r.nextInt(100);
        if (color < 33) {
            eventImage.setBackgroundColor(Color.rgb(199, 198, 197));
        } else if (color < 66) {
            eventImage.setBackgroundColor(Color.rgb(160, 169, 222));
        } else {
            eventImage.setBackgroundColor(Color.rgb(255,120,75));
        }
        */
        return convertView;
    }
}