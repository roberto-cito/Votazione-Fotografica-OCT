package VotazioneFotografica.Model.VotoSquadra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VotoSquadraRepository extends JpaRepository<VotoSquadraEntity,String> {
    int countVotoSquadraEntitiesBySquadraVotata(String squadraVotata);

    void deleteBySquadraVotante(String squadraVotante);

    void deleteBySquadraVotata(String squadraVotata);
}
