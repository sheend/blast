package cse403.blast.Model;

import java.io.Serializable;
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
    private String name;
    private Set<String> eventsCreated;
    private Set<String> eventsAttending;


    public User() {
        this.facebookID = "";
        this.name = "";
        this.eventsCreated = new HashSet<String>();
        this.eventsAttending = new HashSet<String>();
    }

    /**
     * Constructs a new user using their facebook id
     * @param facebookID    user's fb identification
     */
    public User(String facebookID, String name) {
        this.facebookID = facebookID;
        this.name = name;
        this.eventsCreated = new HashSet<String>();
        this.eventsAttending = new HashSet<String>();
        eventsCreated.add("");
        eventsAttending.add("");
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
     * User creates event
     * @param e event to create
     * @return  true if successfully added this to attendees, false otherwise (user is owner)
     */
    public boolean createEvent(Event e) {
        boolean added = eventsCreated.add(e.getId());
        return added;
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
        boolean cancelled = eventsCreated.remove(e.getId());
        return cancelled;
    }

    // Mutators

    /**
     * User leaves event
     * @param e event to leave
     * @return true if successfully left, false otherwise (this is not part of e or this is owner, this should cancelEvent instead of leaveEvent)
     */
    public boolean leaveEvent(Event e) {
        if (!e.getAttendees().contains(this.getFacebookID()) || e.getOwner().equals(this)) { // user is not part of e, or user is owner
            return false;
        }
        e.removeAttendee(this);
        boolean removed = eventsAttending.remove(e.getId());
        return removed;
    }

    /**
     * User attends event
     * @param e event to attend
     * @return  true if successfully added this to attendees, false otherwise (user is owner)
     */
    public boolean attendEvent(Event e) {
        if (eventsCreated.contains(e)) { // user created this event they're trying to attend
            return false;
        }
        e.addAttendee(this);
        boolean added = eventsAttending.add(e.getId());
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
     * Returns user's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the events created by user
     * @return  events created by user
     */
    public Set<String> getEventsCreated() {
        return eventsCreated;
    }

    /**
     * Returns events user is attending
     * @return  events user is attending
     */
    public Set<String> getEventsAttending() {
        return eventsAttending;
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