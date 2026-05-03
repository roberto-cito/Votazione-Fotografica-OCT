package VotazioneFotografica.Model.VotoSquadra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="voti_squadre",schema = "fotografia")
public class VotoSquadraEntity {
    @Id
    @Column(name="squadra_votante",nullable = false,unique = true)
    private String squadraVotante;

    @Column(name="squadra_votata",nullable = false)
    private String squadraVotata;

    public VotoSquadraEntity() {}

    public VotoSquadraEntity(String squadraVotante, String squadraVotata) {
        this.squadraVotante = squadraVotante;
        this.squadraVotata = squadraVotata;
    }

    public String getSquadraVotante() {
        return squadraVotante;
    }

    public void setSquadraVotante(String squadraVotante) {
        this.squadraVotante = squadraVotante;
    }

    public String getSquadraVotata() {
        return squadraVotata;
    }

    public void setSquadraVotata(String squadraVotata) {
        this.squadraVotata = squadraVotata;
    }
}
