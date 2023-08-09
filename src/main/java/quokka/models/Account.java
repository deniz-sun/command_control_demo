package quokka.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_seq")
    @SequenceGenerator(name = "account_id_seq", sequenceName = "account_id_seq", allocationSize = 1)
    private int id;

    private String first_name;
    private String last_name;
    private char[] password;
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_song",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id"))
    private Set<Song> songs = new HashSet<>();

    public String getEmail() {
        return email;
    }

    public Set<Song> getSongs() {
        return songs;
    }


    public Account() {
    }


    public Account(String first_name, String last_name, String email, char[] password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
    }


    public int getId() {
        return id;
    }
    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }
    public char[] getPassword() {return password;}

    @Override
    public String toString() {
        return "\nAccount: [" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ']';
    }
}
