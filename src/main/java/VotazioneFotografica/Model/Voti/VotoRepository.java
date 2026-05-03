package VotazioneFotografica.Model.Voti;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VotoRepository extends JpaRepository<VotoEntity,VotoID> {
    List<VotoEntity> findByGiudice(String giudice);

    List<VotoEntity> findBySquadraAndGiudice(String squadra, String giudice);

    void deleteByGiudice(String giudice);

    void deleteBySquadra(String squadra);

    Optional<VotoEntity> findByGiudiceAndSquadra(String giudice, String squadra);

    List<VotoEntity> findAllBySquadra(String squadra);
}
