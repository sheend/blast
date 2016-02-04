package cse403.blast.Model;

import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by Sheen on 2/2/16.
 */
public class Event {
    private String title;
    private String desc;
    private int limit;
    private Date eventTime;
    private Date creationTime;
    private Set<User> attendees;

    public Event(String title, String desc, int limit, Date eventTime) {
        this.title = title;
        this.desc = desc;
        this.limit = limit;
        this.eventTime = eventTime;
        this.creationTime = new Date(); // initialize to current time
        attendees = new HashSet<User>();
    }

    public boolean addAttendee(User attendee) {
        return attendees.remove(attendee);
    }

    public boolean removeAttendee(User attendee) {
        return attendees.remove(attendee);
    }

    public boolean changeTime(Date time) {
        if (time.getTime() - creationTime.getTime() < 43200000         // must be within 12 hours of creation time
                && time.getTime() - new Date().getTime() >= 3600000) { // new time must be at least 1 after current time
            this.eventTime = time;
            return true;
        }
        return false;
    }

    public void changeDesc(String newDesc) {
        desc = newDesc;
    }

    public boolean changeLimit(int newLimit) {
        if (newLimit < attendees.size()) {
            return false;
        }
        limit = newLimit;
        return true;
    }
}
