package VotazioneFotografica.Model.VotoSquadra;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
public class VotoSquadraService {
    private final VotoSquadraRepository votoSquadraRepository;
    private final SquadraRepository squadraRepository;

    public VotoSquadraService(VotoSquadraRepository votoSquadraRepository, SquadraRepository squadraRepository) {
        this.votoSquadraRepository = votoSquadraRepository;
        this.squadraRepository = squadraRepository;
    }

    public int votiRicevuti(String squadra) {
        return votoSquadraRepository.countVotoSquadraEntitiesBySquadraVotata(squadra);
    }

    public List<VotoSquadraEntity> getAllVoti() {
        return votoSquadraRepository.findAll();
    }

    public HashMap<String,Integer> votiTotali() {
        List<SquadraEntity> squadre=squadraRepository.findAll();
        HashMap<String,Integer> votiTotali=new HashMap<>();
        for(SquadraEntity squadra:squadre) {
            votiTotali.put(squadra.getSquadra(),votiRicevuti(squadra.getSquadra()));
        }
        return votiTotali;
    }

    @Transactional
    public void save(VotoSquadraEntity votoSquadraEntity) {
        votoSquadraRepository.save(votoSquadraEntity);
    }

    @Transactional
    public void removeBySquadra(String squadra) {
        votoSquadraRepository.deleteBySquadraVotante(squadra);
    }
}