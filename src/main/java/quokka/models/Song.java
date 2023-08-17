package quokka.models;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_seq")
    @SequenceGenerator(name = "song_id_seq", sequenceName = "song_id_seq", allocationSize = 1)
    private int id;

    @ManyToMany(mappedBy = "songs")
    private Set<Account> accounts = new HashSet<>();
    private LocalDate song_release_date;
    private String song_title;
    private String song_artist;
    private String song_album;

/*
    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    @Column(name = "hit_count")
    private int hitCount;


*/

    public Song() {}
    public Song(String song_title, String song_artist, String song_album, LocalDate song_release_date){
        this.song_title = song_title;
        this.song_artist = song_artist;
        this.song_album = song_album;
        this.song_release_date = song_release_date;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getSongTitle() {
        return song_title;
    }
    public String getSongArtist() {
        return song_artist;
    }
    public String getSongAlbum() {
        return song_album;
    }
    public LocalDate getReleaseDate() {
        return song_release_date;
    }

    @Override
    public String toString(){
        return "\nSong: [" +
                "id=" + id +
                ", title='" + song_title + '\'' +
                ", album='" + song_album + '\'' +
                ", artist='" + song_artist + '\'' +
                ", release date='" + song_release_date + '\'' +
                ']';
    }
}
