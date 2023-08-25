package quokka.models;

import javax.persistence.*;


@Entity
@Table(name = "areas")
public class Area {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account owner;


    @Column(nullable = false)
    private String coordinates;

    @Column(nullable = false)
    private String color;

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public int getId(){
        return this.id;
    }


}
