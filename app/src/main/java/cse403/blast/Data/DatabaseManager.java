package cse403.blast.Data;

/**
 * Created by Melissa on 2/5/2016.
 */
public class DatabaseManager {

    // Returns all events hosted by the given user
    public Object getMyHostedEvents(int uid) {
        return null;
    }

    // Returns all events attended and to-be-attended
    // by the given user, ordered by most recent
    public Object getMyAttendedEvents(int uid) {
        return null;
    }

    // Return event details of given event id
    public Object getEventDetails(int eid) {
        return null;
    }

    // Returns all users that are attending an event
    public Object getGuestList(int eid) {
        return null;
    }
    
    // Add a new user
    public void addUser(int fid) {

    }

    // Add a new event, make sure to add event
    // to the user table as well
    public void addEvent() {

    }

    // Remove an existing event (make sure to delete events
    // from user table as well)
    public void removeEvent() {

    }

    // Edit an existing event
    public void editEvent() {

    }

    // Update user table
    public void hostEvent(int uid, int eid) {

    }

    // Update both user and event tables
    public void attendEvent(int uid, int eid) {

    }


}
