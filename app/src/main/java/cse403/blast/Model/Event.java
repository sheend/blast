package cse403.blast.Model;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cse403.blast.Support.DateDifference;

/**
 * Created by Sheen on 2/2/16.
 *
 * Mutable class to represent Events.
 * Can change only time, description, and limit. If users want to change more, make a new Blast.
 * These changes are also restricted by certain limitations.
 *
 */
public class Event implements Serializable {
    /*
    Rep invariant:
    owner != null
    title != null && title != ""
    desc != null
    limit >= 1
    eventTime != null && (eventTime - creationTime) < 43200000
    creationTime != null
    attendees.size() >= 1

    public enum Category {
        SOCIAL, FOOD, ACTIVE, ENTERTAINMENT, OTHER
    }
    */

    private String owner;
    private String title;
    private String desc;
    private String location;
    private int limit;
    //private Category category;
    private Date eventTime;
    private Date creationTime;
    private Set<String> attendees;
    private String firebaseID;

    /**
     * Empty constructor
     */
    public Event() {
        this.owner = "";
        this.title = "";
        this.desc = "";
        this.location = "";
        this.limit = 0;
        this.eventTime = new Date();
        this.creationTime = new Date(); // initialize to current time
        this.attendees = new HashSet<String>();
        this.firebaseID = "";
        //this.category = Category.ACTIVE; //TODO: change this
    }


    /** Constructs an event using the given attributes
     *
     * @param owner owner of event
     * @param title title of event
     * @param desc  description of event
     * @param limit limit of people for event
     * @param eventTime time event will occur
     */
    public Event(String owner, String title, String desc, String location, int limit, Date eventTime) {
        this.owner = owner;
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.limit = limit;
        //this.category = Category.ACTIVE; //TODO: Change this
        this.eventTime = eventTime;
        this.creationTime = new Date(); // initialize to current time
        this.attendees = new HashSet<String>();
        attendees.add("");
        this.firebaseID = "";
        // TODO: add the event to owner's list through client code
        // TODO: cannot call method from constructor because of firebase parsing
        // owner.addCreatedEvent(this); // add this event to owner's list of created events
    }

    /**
     * Sets an event's toString to be its title
     * @return this's title
     */
    public String toString() {
        return title;
    }

    /**
     * Determines if two events are equal (if title, desc, and eventTime are the same)
     * @param o event to compare to
     * @return true if both events have the same title, desc, and event time, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event)) {
            return false;
        }
        Event e = (Event) o;
        return this.title.equals(e.title)
                && this.desc.equals(e.desc)
                && this.eventTime.equals(e.eventTime);
    }

    /**
     * Computes a unique hashcode for this
     * @return unique hashcode for this
     */
    @Override
    public int hashCode() {
        return title.hashCode() * desc.hashCode() * eventTime.hashCode();
    }

    // Mutators

    /**
     * Add attendee to event
     * @param attendee  new user attending event
     * @return  true if successfully added, false otherwise
     */
    public boolean addAttendee(User attendee) {
        boolean added = attendees.add(attendee.getFacebookID());
        checkRep();
        return added;
    }

    /**
     * Remove attendee from event
     * @param attendee  user who wants to leave event
     * @return  true if successfully removed, false otherwise
     */
    public boolean removeAttendee(User attendee) {
        boolean removed = attendees.remove(attendee.getFacebookID());
        checkRep();
        return removed;
    }

    //Getters

    /**
     * Return owner of event
     * @return  owner of event
     */
    public String getOwner() {
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
     * Return location of event
     * @return  location of event
     */
    public String getLocation() { return location; }

    /**
     * Return limit of event
     * @return limit of event
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Return category of event
     * @return category (enum) of event

    public Category getCategory() {
        return (category == null) ? Category.ACTIVE : category; //TODO: backup plan
    }

    /**
     * Return category of event
     * @return category (enum) of event
     * //TODO: Add colors to the categories/add pictures?
     *
    public int getCategoryColor() {
        switch (getCategory()) {
            case ACTIVE:
                return Color.rgb(255, 26, 0);
            case ENTERTAINMENT:
                return Color.rgb(255,26,0);
            case FOOD:
                return Color.rgb(255, 26, 0);
            case SOCIAL:
                return Color.rgb(130,143,212); // light blue
            case OTHER:
                return Color.rgb(255,26,0);
            default:
                return Color.rgb(128,128,128);
        }
    }
    */

    /**
     * Return time event is occurring
     * For use when the Date object is needed
     * @return  time event is occurring
     */
    public Date getEventTime() {
        return new Date(eventTime.getTime());
    }

    /**
     * Return toString for the date
     * For use when the time is to be displayed for the event details
     * @return
     */
    public String getEventTimeString() {
        SimpleDateFormat time = new SimpleDateFormat("KK:mm a");
        SimpleDateFormat day = new SimpleDateFormat("dd MMM");

        return time.format(eventTime) + " on " + day.format(eventTime);
    }

    /**
     * Return the toString for the difference in time between now and then
     * For use when the time is to be
     * @return
     */
    public String getTimeDifference() {
        Date today = new Date();
        return DateDifference.getDifferenceString(today, eventTime);
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
    public Set<String> getAttendees() {
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
            checkRep();
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
        checkRep();
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
        checkRep();
        return true;
    }

    public void setId(String id) {
        firebaseID = id;
    }

    public String getId() {
        return firebaseID;
    }

    /**
     * Check that the representation invariant has not been broken.
     */
    private void checkRep() {
        assert(owner != null);
        assert(title != null && title != "");
        assert(desc != null); // desc can be an empty String, just not null
        assert(limit >= 1); // must have at least one other attendee (not including owner)
        assert(eventTime != null && (eventTime.getTime() - creationTime.getTime()) < 43200000);
        assert(creationTime != null);
        assert(attendees.size() >= 1); // owner counts as one attendee
    }
}
