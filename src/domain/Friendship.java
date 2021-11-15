package domain;

import java.time.LocalDate;
import java.util.Objects;

public class Friendship {
    //a friendship contains as attributes the emails of the users
    private String email1, email2;
    private FRIENDSHIPSTATE state;
    private LocalDate date;
    public Friendship(String e1, String e2) {
        this.email1 = e1;
        this.email2 = e2;
        state = FRIENDSHIPSTATE.PENDING;
        date = null;
    }

    public Friendship(User u1, User u2) {
        this.email1 = u1.getEmail();
        this.email2 = u2.getEmail();
        state = FRIENDSHIPSTATE.PENDING;
        date = null;
    }

    @Override
    public String toString() {
        return email1 + " --- " + email2;
    }

    @Override
    public int hashCode() {
        return (email1 + email2).hashCode();
    }

    /**
     * @return the email of the first user of the friendship
     */
    public String getFirst() { return email1; }

    /**
     * @return the email of the second user of the friendship
     */
    public String getSecond() { return email2; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return (Objects.equals(email1, that.email1) && Objects.equals(email2, that.email2));
    }

    /**
     * Gets the state of the friendship object
     * @return - ENUM type FRIENDSHIPSTATE
     */
    public FRIENDSHIPSTATE getState() {
        return state;
    }

    /**
     * Sets the state of the friendship object
     * @param state - enum type FRIENDSHIPSTATE
     */
    public void setState(FRIENDSHIPSTATE state){
        this.state = state;

    }

    /**
     *
     * @return - return the date when the friend request was accepted
     */
    public LocalDate getDate() {
        return date;
    }

    /** Sets the date of the friendship object
     * @param date - LocalDate
     */
    public void setDate(LocalDate date){
        this.date = date;
    }
}
