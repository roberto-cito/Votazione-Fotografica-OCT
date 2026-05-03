package VotazioneFotografica.Model.Squadre;

import VotazioneFotografica.Model.Voti.VotoRepository;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SquadraService {
    private final SquadraRepository squadraRepository;
    private final VotoRepository votoRepository;
    private final VotoSquadraRepository votoSquadraRepository;

    public SquadraService(SquadraRepository squadraRepository, VotoRepository votoRepository, VotoSquadraRepository votoSquadraRepository) {
        this.squadraRepository = squadraRepository;
        this.votoRepository = votoRepository;
        this.votoSquadraRepository = votoSquadraRepository;
    }

    public List<SquadraEntity> getAll() {
        return squadraRepository.findAll();
    }

    public List<SquadraEntity> getAllWhoUploaded() {
        List<SquadraEntity> squadre=squadraRepository.findAll();
        return squadre.stream().filter(squadraEntity -> squadraEntity.isHas_uploaded()).collect(Collectors.toList());
    }

    public List<SquadraEntity> getAllWhoUploadedAndItsNotMe(SquadraEntity me) {
        List<SquadraEntity> squadre=squadraRepository.findAllByIstitutoIsNotLike(me.getIstituto());
        return squadre.stream().filter(squadraEntity -> squadraEntity.isHas_uploaded() && !squadraEntity.getSquadra().equals(me.getSquadra())).collect(Collectors.toList());
    }

    public boolean isMyIstituto(SquadraEntity me, String selezionata) {
        return squadraRepository.findBySquadra(selezionata).get().getIstituto().equals(me.getIstituto());
    }

    public Optional<SquadraEntity> getByName(String squadra) {
        return squadraRepository.findBySquadra(squadra);
    }

    @Transactional
    public void save(SquadraEntity squadra) {
        squadraRepository.save(squadra);
    }

    @Transactional
    public void remove(String username) {
        votoRepository.deleteBySquadra(username);
        votoSquadraRepository.deleteBySquadraVotata(username);
        votoSquadraRepository.deleteBySquadraVotante(username);
        SquadraEntity squadra=getByName(username).orElseThrow();
        if(squadra.isHas_uploaded()) {
            try {
                Files.delete(Path.of(squadra.getPath_foto()));
            } catch (Exception e) {
                System.err.println("Errore durante la cancellazione della foto");
                e.printStackTrace();
            }
        }
        squadraRepository.delete(getByName(username).orElseThrow());
    }

    @Transactional
    public void removeFoto(String username) {
        votoRepository.deleteBySquadra(username);
        votoSquadraRepository.deleteBySquadraVotata(username);
        SquadraEntity squadra=getByName(username).orElseThrow();
        if(squadra.isHas_uploaded()) {
            try {
                Files.delete(Path.of(squadra.getPath_foto()));
            } catch (Exception e) {
                System.err.println("Errore durante la cancellazione della foto");
                e.printStackTrace();
            }
        }
        squadra.setHas_uploaded(false);
        squadra.setPath_foto("");
        squadraRepository.save(squadra);
    }

    @Transactional
    public void removeAll() {
        votoRepository.deleteAll();
        votoSquadraRepository.deleteAll();
        for(SquadraEntity squadra:squadraRepository.findAll()) {
            if(squadra.isHas_uploaded()) {
                try {
                    Files.delete(Path.of(squadra.getPath_foto()));
                } catch (Exception e) {
                    System.err.println("Errore durante la cancellazione della foto");
                    e.printStackTrace();
                }
            }
        }
        squadraRepository.deleteAll();
    }
}
