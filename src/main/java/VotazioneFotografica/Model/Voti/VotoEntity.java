package VotazioneFotografica.Model.Voti;

import jakarta.persistence.*;

@Entity
@Table(name="voti_giudici",schema = "fotografia")
@IdClass(VotoID.class)
public class VotoEntity {
    @Id
    @Column(name="giudice",nullable = false)
    private String giudice;

    @Id
    @Column(name="squadra",nullable = false)
    private String squadra;

    @Column(name="voto",nullable = false)
    private float punteggio;

    public VotoEntity() {
    }

    public VotoEntity(String giudice, String squadra, float punteggio) {
        this.giudice = giudice;
        this.squadra = squadra;
        this.punteggio = punteggio;
    }

    public String getGiudice() {
        return giudice;
    }

    public void setGiudice(String giudice) {
        this.giudice = giudice;
    }

    public String getSquadra() {
        return squadra;
    }

    public void setSquadra(String squadra) {
        this.squadra = squadra;
    }

    public float getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(float punteggio) {
        this.punteggio = punteggio;
    }
}
