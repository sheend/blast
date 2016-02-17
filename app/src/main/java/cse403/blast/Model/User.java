package cse403.blast.Model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sheen on 2/2/16.
 *
 * Mutable class representing a user.
 * Can modify events user is attending/add to events created.
 */
public class User implements Serializable {
    /*
     Rep invariant:
      facebookID != null && facebookID != ""
      eventsCreated != null && eventsCreated.size() >= 0
      eventsAttending != null && eventsAttending.size() >= 0
     */

    private String facebookID;
    private Set<Event> eventsCreated;
    private Set<Event> eventsAttending;


    public User() {

    }

    /**
     * Constructs a new user using their facebook id
     * @param facebookID    user's fb identification
     */
    public User(String facebookID) {
        this.facebookID = facebookID;
        eventsCreated = new HashSet<Event>();
        eventsAttending = new HashSet<Event>();
        checkRep();
    }

    /**
     * Determines whether two Users are equal, or are the same person.
     * @param o other user to compare to
     * @return true if their facebookId's are exactly the same, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }
        return this.facebookID.equals(((User) o).facebookID);
    }

    /**
     * Produces a unique hashcode per user
     * @return a unique hashcode for user
     */
    @Override
    public int hashCode() {
        return facebookID.hashCode();
    }

    /**
     * User creates event, now add to list of events created and add self to event's attendees
     * @return  event that was created
     */
    // TODO: is it ok to only allow creation of events through Users?
    // TODO: update database
    // TODO: why will this only work when eventsCreated.add(event) is in an assert?
    public Event createEvent(String title, String desc, int limit, Date eventTime) {
        Event event = new Event(this, title, desc, limit, eventTime);
        assert(eventsCreated.add(event));
        checkRep();
        return event;
    }

    /**
     * User cancelled an event, now remove from list of events created
     * @param e event user wants to cancel
     * @return  true if successfully removed (e was created by user), false otherwise (e was not created by user)
     */
    // TODO: not sufficient to just remove an event from a user's list of created events...
    // TODO: indicate to MainActivity to not display event anymore?
    // TODO: indicate to attendees that event has been cancelled?
    public boolean cancelEvent(Event e) {
        boolean cancelled = eventsCreated.remove(e);
        checkRep();
        return cancelled;
    }

    // Mutators

    /**
     * User leaves event
     * @param e event to leave
     * @return true if successfully left, false otherwise (this is not part of e or this is owner, this should cancelEvent instead of leaveEvent)
     */
    public boolean leaveEvent(Event e) {
        if (!e.getAttendees().contains(this) || e.getOwner().equals(this)) { // user is not part of e, or user is owner
            return false;
        }
        e.removeAttendee(this);
        boolean removed = eventsAttending.remove(e);
        checkRep();
        return removed;
    }

    /**
     * User attends event
     * @param e event to attend
     * @return  true if successfully added this to attendees, false otherwise (user is owner)
     */
    // TODO: how to handle two-way connection? Should User be updating event, or event updating user?
    public boolean attendEvent(Event e) {
        if (eventsCreated.contains(e)) { // user created this event they're trying to attend
            return false;
        }
        e.addAttendee(this);
        boolean added = eventsAttending.add(e);
        checkRep();
        return added;
    }

    // Getters

    /**
     * Returns user's facebookID
     * @return  facebookID
     */
    public String getFacebookID() {
        return facebookID;
    }

    /**
     * Returns the events created by user
     * @return  events created by user
     */
    public Set<Event> getEventsCreated() {
        return Collections.unmodifiableSet(eventsCreated);
    }

    /**
     * Returns events user is attending
     * @return  events user is attending
     */
    public Set<Event> getEventsAttending() {
        return Collections.unmodifiableSet(eventsAttending);
    }

    /**
     * Checks that the rep invariant hasn't been broken.
     */
    private void checkRep() {
        assert(facebookID != null && facebookID != ""); // every user is a facebook user
        assert(eventsCreated != null && eventsCreated.size() >= 0);
        assert(eventsAttending != null && eventsAttending.size() >= 0);
    }
}