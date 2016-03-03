package cse403.blast.Data;

/**
 * Created by Sheen on 2/26/16.
 */

import android.location.Location;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aixin on 2/26/16.
 */
public class LocationHandler {
    private final String API_KEY = "AIzaSyC1Fh0M4ufNx0IPFgupHMsPoiKEbW7BFlo";
    private final String QUERY_ORIG_CALL = "https://maps.googleapis.com/maps/api/place/queryautocomplete/json?";
    private final String PLACE_ID_ORIG_CALL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private final String GEO_CODING_ORIG_CALL = "https://maps.googleapis.com/maps/api/geocode/json?";

    public Map<String, Location> getMatchingLoc(String input, Location loc) {
        Map<String, Location> ret = new HashMap<String, Location>();

        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String city = URLEncoder.encode(getUserCity(loc), "UTF-8");
            input = URLEncoder.encode(input, "UTF-8");
            String req = QUERY_ORIG_CALL + "key=" + API_KEY + "&input=" + input +"+near%20" + city;

            JSONObject json = getJSONFromReq(req);
            JSONArray results = json.getJSONArray("predictions");

            for (int i = 0; i < results.length(); i++) {
                JSONObject r = (JSONObject) results.get(i);
                String description = r.getString("description");
                Location l = getLocation(r.getString("place_id"));
                ret.put(description, l);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Map<String, Location> getLocalEvents(Map<String, Location> events, Location loc) {
        Map<String, Location> ret = new HashMap<>();
        for (String s: events.keySet()) {
            if (isNearby(events.get(s), 100, loc))
                ret.put(s,  events.get(s));
        }
        return ret;
    }

    private String getUserCity(Location loc) throws IOException, JSONException {
        String req = GEO_CODING_ORIG_CALL + "latlng=" + loc.getLatitude() + "," + loc.getLongitude()
                + "&key=" + API_KEY;
        JSONObject detail = getJSONFromReq(req);
        return getUserCityHelper(detail);
    }

    private String getUserCityHelper(JSONObject obj) throws JSONException {
        JSONArray arr = obj.getJSONArray("results");
        if (arr.length() < 1)
            throw new IllegalArgumentException();
        JSONArray addressComp = arr.getJSONObject(0).getJSONArray("address_components");

        if (addressComp.length() < 5)
            throw new IllegalArgumentException();
        obj = addressComp.getJSONObject(addressComp.length() - 5);
        return obj.getString("long_name");
    }

    private Location getLocation(String place_id) throws IOException, JSONException {
        String req = PLACE_ID_ORIG_CALL + "key=" + API_KEY + "&placeid=" + place_id;
        JSONObject detail = getJSONFromReq(req);
        return getLocationHelper(detail);
    }

    private Location getLocationHelper(JSONObject obj) throws JSONException {
        obj = (JSONObject) obj.get("result");
        obj = (JSONObject) obj.get("geometry");
        obj = (JSONObject) obj.get("location");
        double lat = obj.getDouble("lat");
        double lng = obj.getDouble("lng");

        Location loc = new Location("eventLocation");
        loc.setLatitude(lat);
        loc.setLongitude(lng);

        return loc;
    }

    private JSONObject getJSONFromReq(String req) throws IOException, JSONException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        URL url = new URL(req);
        URLConnection urlConn = url.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            sb.append(line);
            Log.i("TAG", line);
        }

        br.close();

        return new JSONObject(sb.toString());
    }

    private boolean isNearby(Location eventLoc, int mileDist, Location userLoc) {
        return ((double) userLoc.distanceTo(eventLoc) / 1609.34 <= mileDist);
    }
}