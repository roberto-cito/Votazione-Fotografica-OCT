package VotazioneFotografica.Model.Voti;

import java.io.Serializable;
import java.util.Objects;

public class VotoID implements Serializable {
    private String giudice;
    private String squadra;

    public VotoID() {
    }

    public VotoID(String giudice, String squadra) {
        this.giudice = giudice;
        this.squadra = squadra;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VotoID votoID = (VotoID) o;
        return Objects.equals(giudice, votoID.giudice) && Objects.equals(squadra, votoID.squadra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(giudice, squadra);
    }
}
