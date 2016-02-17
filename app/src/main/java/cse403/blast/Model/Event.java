package cse403.blast.Model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;


//public class Event {
//    private User owner;
//    private String title;
//    private String desc;
//    private int limit;
//    private Date eventTime;
//    private Date creationTime;
//    private Set<User> attendees;
//
//    public Event() {
//    }
//
//    public Event(User owner, String title, String desc, int limit, Date eventTime, Date creationTime, Set<User> attendees) {
//        this.owner = owner;
//        this.title = title;
//        this.desc = desc;
//        this.limit = limit;
//        this.eventTime = eventTime;
//        this.creationTime = creationTime;
//        this.attendees = attendees;
//    }
//
//    public Event(User owner, String title, String desc, int limit, Date eventTime) {
//        this.owner = owner;
//        this.title = title;
//        this.desc = desc;
//        this.limit = limit;
//        this.eventTime = eventTime;
//        this.creationTime = new Date(); // initialize to current time
//        attendees = new HashSet<User>();
//        owner.addCreatedEvent(this); // add this event to owner's list of created events
//    }
//
//    public User getOwner() {
//        return owner;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public String getDesc() {
//        return desc;
//    }
//
//    public int getLimit() {
//        return limit;
//    }
//
//    public Date getEventTime() {
//        return eventTime;
//    }
//
//    public Date getCreationTime() {
//        return creationTime;
//    }
//
//    public Set<User> getAttendees() {
//        return attendees;
//    }
//
//    public boolean addAttendee(User attendee) {
//        return attendees.remove(attendee);
//    }
//
//    public boolean removeAttendee(User attendee) {
//        return attendees.remove(attendee);
//    }
//}



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
     */

    private User owner;
    private String title;
    private String desc;
    private String location;
    private int limit;
    private Date eventTime;
    private Date creationTime;
    private Set<User> attendees;

    /**
     * Empty constructor
     */
    public Event() {
        this.creationTime = new Date(); // initialize to current time
        this.attendees = new HashSet<User>();


    }


    /** Constructs an event using the given attributes
     *
     * @param owner owner of event
     * @param title title of event
     * @param desc  description of event
     * @param limit limit of people for event
     * @param eventTime time event will occur
     */
    public Event(User owner, String title, String desc, String location, int limit, Date eventTime) {
        this.owner = owner;
        this.title = title;
        this.desc = desc;
        this.location = location;
        this.limit = limit;
        this.eventTime = eventTime;
        this.creationTime = new Date(); // initialize to current time
        this.attendees = new HashSet<User>();
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
        boolean added = attendees.add(attendee);
        checkRep();
        return added;
    }

    /**
     * Remove attendee from event
     * @param attendee  user who wants to leave event
     * @return  true if successfully removed, false otherwise
     */
    public boolean removeAttendee(User attendee) {
        boolean removed = attendees.remove(attendee);
        checkRep();
        return removed;
    }

    //Getters

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
