package VotazioneFotografica.Model.VotoSquadra;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoSquadraServiceTest {

    @Mock
    private VotoSquadraRepository votoSquadraRepository;

    @Mock
    private SquadraRepository squadraRepository;

    @InjectMocks
    private VotoSquadraService votoSquadraService;

    @Test
    void votiRicevuti() {
        when(votoSquadraRepository.countVotoSquadraEntitiesBySquadraVotata("Squadra A")).thenReturn(5);
        int result = votoSquadraService.votiRicevuti("Squadra A");
        assertEquals(5, result);
    }

    @Test
    void getAllVoti() {
        VotoSquadraEntity v = new VotoSquadraEntity("votante", "votata");
        when(votoSquadraRepository.findAll()).thenReturn(Collections.singletonList(v));
        List<VotoSquadraEntity> result = votoSquadraService.getAllVoti();
        assertEquals(1, result.size());
    }

    @Test
    void votiTotali() {
        SquadraEntity s1 = new SquadraEntity("Squadra A", "Real A", "Ist A");
        SquadraEntity s2 = new SquadraEntity("Squadra B", "Real B", "Ist B");

        when(squadraRepository.findAll()).thenReturn(Arrays.asList(s1, s2));
        when(votoSquadraRepository.countVotoSquadraEntitiesBySquadraVotata("Squadra A")).thenReturn(10);
        when(votoSquadraRepository.countVotoSquadraEntitiesBySquadraVotata("Squadra B")).thenReturn(3);

        HashMap<String, Integer> result = votoSquadraService.votiTotali();

        assertEquals(2, result.size());
        assertEquals(10, result.get("Squadra A"));
        assertEquals(3, result.get("Squadra B"));
    }

    @Test
    void save() {
        VotoSquadraEntity v = new VotoSquadraEntity("votante", "votata");
        votoSquadraService.save(v);
        verify(votoSquadraRepository, times(1)).save(v);
    }
}
