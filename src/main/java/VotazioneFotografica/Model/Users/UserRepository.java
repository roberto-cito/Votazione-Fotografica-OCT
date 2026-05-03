package VotazioneFotografica.Model.Users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> getAllByRole(String role);
}

