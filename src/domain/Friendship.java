package domain;

import java.util.Objects;

public class Friendship {
    //o prietenie are ca atribute email-urile utilizatorilor
    private String email1, email2;

    public Friendship(String e1, String e2) {
        if (e1.compareTo(e2) > 0) {
            String aux = e1;
            e1 = e2;
            e2 = aux;
        }
        this.email1 = e1;
        this.email2 = e2;
    }

    public Friendship(User u1, User u2) {
        if (u1.getEmail().compareTo(u2.getEmail()) > 0) {
            User aux = u1;
            u1 = u2;
            u2 = aux;
        }
        this.email1 = u1.getEmail();
        this.email2 = u2.getEmail();
    }

    @Override
    public String toString() {
        return email1 + " --- " + email2;
    }

    @Override
    public int hashCode() {
        return (email1 + email2).hashCode();
    }

    public String getFirst() { return email1; }
    public String getSecond() { return email2; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return (Objects.equals(email1, that.email1) && Objects.equals(email2, that.email2));
    }
}
