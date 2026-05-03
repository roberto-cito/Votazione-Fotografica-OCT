package VotazioneFotografica.Controller.Admin.ManageUsers;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AddSquadraForm {
    @NotEmpty(message = "Lo username non può essere vuoto")
    @Size(max = 50, message = "La lunghezza massima è 50 caratteri")
    private String username;

    @NotEmpty(message = "Il nome della squadra non può essere vuoto")
    @Size(max = 255, message = "La lunghezza massima è 255 caratteri")
    private String realName;

    @NotEmpty(message = "IL nome dell'istituto non può essere vuoto")
    @Size(max = 255, message = "La lunghezza massima è 255 caratteri")
    private String istituto;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIstituto() {
        return istituto;
    }

    public void setIstituto(String istituto) {
        this.istituto = istituto;
    }
}
