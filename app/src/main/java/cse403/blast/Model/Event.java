package cse403.blast.Model;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by Sheen on 2/2/16.
 *
 * Mutable class to represent Events.
 * Can change only time, description, and limit. If users want to change more, make a new Blast.
 * These changes are also restricted by certain limitations.
 *
 */
public class Event {
    private User owner;
    private String title;
    private String desc;
    private int limit;
    private Date eventTime;
    private Date creationTime;
    private Set<User> attendees;

    /** Constructs an event using the given attributes
     *
     * @param owner owner of event
     * @param title title of event
     * @param desc  description of event
     * @param limit limit of people for event
     * @param eventTime time event will occur
     */
    public Event(User owner, String title, String desc, int limit, Date eventTime) {
        this.owner = owner;
        owner.addCreatedEvent(this); // add this event to owner's list of created events
        this.title = title;
        this.desc = desc;
        this.limit = limit;
        this.eventTime = eventTime;
        this.creationTime = new Date(); // initialize to current time
        attendees = new HashSet<User>();
    }

    public String toString() {
        return title;
    }

    // Mutators

    /**
     * Add attendee to event
     * @param attendee  new user attending event
     * @return  true if successfully added, false otherwise
     */
    public boolean addAttendee(User attendee) {
        return attendees.remove(attendee);
    }

    /**
     * Remove attendee from event
     * @param attendee  user who wants to leave event
     * @return  true if successfully removed, false otherwise
     */
    public boolean removeAttendee(User attendee) {
        return attendees.remove(attendee);
    }

    // Getters

    /**
     * Return owner of event
     * @return  owner of event
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Return title of event
     * @return title of event
     */
    public String getTitle() {
        return title;
    }

    /**
     * Return description of event
     * @return  description of event
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Return limit of event
     * @return limit of event
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Return time event is occurring
     * @return  time event is occurring
     */
    public Date getEventTime() {
        return new Date(eventTime.getTime());
    }

    /**
     * Return time that event was initially created
     * @return  time event was created
     */
    public Date getCreationTime() {
        return new Date(creationTime.getTime());
    }

    /**
     * Current people attending event
     * @return  list of attendees
     */
    public Set<User> getAttendees() {
        return Collections.unmodifiableSet(attendees);
    }

    // Setters

    /**
     * Change time that event is occurring
     * @param newTime  time to change event occurrence to
     * @return  true if new time is within 12 hours of creation time
     *          and if it is at least 1 hour after current time, false otherwise
     */
    public boolean changeTime(Date newTime) {
        if (newTime.getTime() - creationTime.getTime() < 43200000         // must be within 12 hours of creation time
                && newTime.getTime() - new Date().getTime() >= 3600000) { // new time must be at least 1 after current time
            this.eventTime = newTime;
            return true;
        }
        return false;
    }

    /**
     * Change description of event
     * @param newDesc   new description of event
     */
    public void changeDesc(String newDesc) {
        desc = newDesc;
    }

    /**
     * Change limit of event
     * @param newLimit  new limit of event
     * @return true if new limit is greater than or equal to current number of attendees, false otherwise
     */
    public boolean changeLimit(int newLimit) {
        if (newLimit < attendees.size()) {
            return false;
        }
        limit = newLimit;
        return true;
    }
}
