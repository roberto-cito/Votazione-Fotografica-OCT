package VotazioneFotografica.Model.Voti;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VotoService {
    private final VotoRepository votoRepository;

    public VotoService(VotoRepository votoRepository) {
        this.votoRepository = votoRepository;
    }

    public List<VotoEntity> getAll() {
        return votoRepository.findAll();
    }

    public Optional<VotoEntity> getByVotoID(VotoID votoID) {
        return votoRepository.findById(votoID);
    }

    public List<VotoEntity> getByGiudice(String giudice) {
        return votoRepository.findByGiudice(giudice);
    }

    public VotoEntity save(VotoEntity votoEntity) {
        return votoRepository.save(votoEntity);
    }

    public void aggiungiVoto(String giudice, String squadra, float punteggio) {
        VotoEntity voto=new VotoEntity(giudice,squadra,punteggio);
        votoRepository.save(voto);
    }

    public boolean hasVoted(String giudice, String squadra) {
        return votoRepository.findByGiudiceAndSquadra(giudice, squadra).isPresent();
    }

    public Optional<VotoEntity> getByGiudiceAndSquadra(String giudice, String squadra) {
        return votoRepository.findByGiudiceAndSquadra(giudice, squadra);
    }

    public void removeVoto(VotoEntity votoEntity) {
        votoRepository.delete(votoEntity);
    }

    public List<VotoEntity> getAllVotiBySquadra(String squadra) {
        return votoRepository.findAllBySquadra(squadra);
    }
}
