package VotazioneFotografica.Controller.Admin.ManageUsers;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class AddUserForm {
    @NotEmpty(message = "Lo username non può essere vuoto")
    @Size(max = 50, message = "La lunghezza massima è 50 caratteri")
    private String username;
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
