package cse403.blast.Support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.acl.Group;
import java.util.List;

import cse403.blast.R;

    /**
     * Created by Sheen on 3/4/16.
     */
    public class LocationAdapter extends ArrayAdapter<String> {

        private List<String> objects;

        public LocationAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.objects = objects;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
//
//        if (v == null) {
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = inflater.inflate(listItemID, null);
//        }
//
//        String g = objects.get(position);
//        ImageView imgProfile = (ImageView) v.findViewById(R.id.img_profile_memberlist);
//        TextView tvUser = (TextView) v.findViewById(R.id.lbl_name_memberlist);
//
//        if (imgProfile != null) {
//            imgProfile.setImageDrawable(g.getProfilePicture());
//        }
//        if (tvUser != null) {
//            tvUser.setText(g.getName());
//        }

        return v;
    }
}