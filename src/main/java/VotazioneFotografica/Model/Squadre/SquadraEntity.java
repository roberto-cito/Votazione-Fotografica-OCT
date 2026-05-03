package VotazioneFotografica.Model.Squadre;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "squadre", schema = "fotografia")
public class SquadraEntity {
    @Id
    @Column(name = "squadra", nullable = false, unique = true)
    private String squadra;

    @Column(name = "has_uploaded", nullable = false)
    private boolean has_uploaded;

    @Column(name = "foto")
    private String path_foto;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(name = "has_voted",nullable = false)
    private boolean has_voted;

    @Column(name = "istituto")
    private String istituto;

    public SquadraEntity() {
    }

    public SquadraEntity(String squadra, String realName, String istituto) {
        this.squadra=squadra;
        this.has_uploaded=false;
        this.path_foto="";
        this.realName=realName;
        this.has_voted=false;
        this.istituto=istituto;
    }

    public SquadraEntity(String squadra, boolean has_uploaded, String path_foto, String real_name, boolean has_voted, String istituto) {
        this.squadra = squadra;
        this.has_uploaded = has_uploaded;
        this.path_foto = path_foto;
        this.realName = real_name;
        this.has_voted = has_voted;
        this.istituto = istituto;
    }

    public String getSquadra() {
        return squadra;
    }

    public void setSquadra(String squadra) {
        this.squadra = squadra;
    }

    public boolean isHas_uploaded() {
        return has_uploaded;
    }

    public void setHas_uploaded(boolean has_uploaded) {
        this.has_uploaded = has_uploaded;
    }

    public String getPath_foto() {
        return path_foto;
    }

    public void setPath_foto(String path_foto) {
        this.path_foto = path_foto;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isHas_voted() {
        return has_voted;
    }

    public void setHas_voted(boolean has_voted) {
        this.has_voted = has_voted;
    }

    public String getIstituto() {
        return istituto;
    }

    public void setIstituto(String istituto) {
        this.istituto = istituto;
    }
}
