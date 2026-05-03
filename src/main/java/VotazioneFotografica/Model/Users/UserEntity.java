package VotazioneFotografica.Model.Users;

import jakarta.persistence.*;

@Entity
@Table(name = "utenti", schema = "fotografia")
public class UserEntity {
    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

    @Column(name = "role", nullable = false)
    private String role;

    public UserEntity() {}

    public UserEntity(String username, String hashPassword, String role) {
        this.username = username;
        this.hashPassword = hashPassword;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
