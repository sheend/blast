package cse403.blast.Model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sheen on 2/2/16.
 *
 * Mutable class representing a user.
 * Can modify events user is attending/add to events created.
 */
public class User {
    private String facebookID;
    private Set<Event> eventsCreated;
    private Set<Event> eventsAttending;

    /**
     * Constructs a new user using their facebook id
     * @param facebookID    user's fb identification
     */
    public User(String facebookID) {
        this.facebookID = facebookID;
        eventsCreated = new HashSet<Event>();
        eventsAttending = new HashSet<Event>();
    }

    // Mutators

    /**
     * User leaves event
     * @param e event to leave
     * @return true if successfully left, false otherwise
     */
    public boolean leaveEvent(Event e) {
        e.removeAttendee(this); // need to decide: have user or event class handle this two-way connection?
        return eventsAttending.remove(e);
    }

    /**
     * User attends event
     * @param e event to attend
     * @return  true if successfully added to attendees, false otherwise
     */
    public boolean attendEvent(Event e) {
        e.addAttendee(this); // need to decide: have user or event class handle this two-way connection?
        return eventsAttending.add(e);
    }

    /**
     * User created an event, now add to list of events created
     * @param e event user created
     * @return  true if successfully added, false otherwise
     */
    public boolean addCreatedEvent(Event e) {
        return eventsCreated.add(e);
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
}
