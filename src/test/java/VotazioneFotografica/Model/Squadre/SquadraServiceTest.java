package VotazioneFotografica.Model.Squadre;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SquadraServiceTest {

    @Mock
    private SquadraRepository squadraRepository;

    @InjectMocks
    private SquadraService squadraService;

    private SquadraEntity squadra1;
    private SquadraEntity squadra2;

    @BeforeEach
    void setUp() {
        // Constructor: String squadra, boolean has_uploaded, String path_foto, String real_name, boolean has_voted, String istituto
        squadra1 = new SquadraEntity("Squadra A", true, "pathA", "Real A", false, "Ist A");
        squadra2 = new SquadraEntity("Squadra B", false, "pathB", "Real B", false, "Ist B");
    }

    @Test
    void getAll() {
        when(squadraRepository.findAll()).thenReturn(Arrays.asList(squadra1, squadra2));
        List<SquadraEntity> result = squadraService.getAll();
        assertEquals(2, result.size());
    }

    @Test
    void getAllWhoUploaded() {
        when(squadraRepository.findAll()).thenReturn(Arrays.asList(squadra1, squadra2));
        List<SquadraEntity> result = squadraService.getAllWhoUploaded();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Squadra A", result.get(0).getSquadra());
    }

    @Test
    void getAllWhoUploaded_Empty() {
        when(squadraRepository.findAll()).thenReturn(Collections.emptyList());
        List<SquadraEntity> result = squadraService.getAllWhoUploaded();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByName() {
        when(squadraRepository.findBySquadra("Squadra A")).thenReturn(Optional.of(squadra1));
        Optional<SquadraEntity> result = squadraService.getByName("Squadra A");
        assertTrue(result.isPresent());
        assertEquals("Squadra A", result.get().getSquadra());
    }

    @Test
    void save() {
        squadraService.save(squadra1);
        verify(squadraRepository, times(1)).save(squadra1);
    }
}
