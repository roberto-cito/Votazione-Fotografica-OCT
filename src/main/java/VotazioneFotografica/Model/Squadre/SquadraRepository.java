package VotazioneFotografica.Model.Squadre;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SquadraRepository extends JpaRepository<SquadraEntity, String> {
    Optional<SquadraEntity> findBySquadra(String squadra);

    List<SquadraEntity> findAllByIstitutoIsNotLike(String istituto);
}
