package VotazioneFotografica.Model.Voti;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private VotoService votoService;

    private VotoEntity voto1;

    @BeforeEach
    void setUp() {
        // VotoEntity(String giudice, String squadra, float punteggio)
        voto1 = new VotoEntity("giudice1", "squadra1", 8.5f);
    }

    @Test
    void getAll() {
        when(votoRepository.findAll()).thenReturn(Collections.singletonList(voto1));
        List<VotoEntity> result = votoService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void getByVotoID() {
        VotoID votoID = new VotoID("giudice1", "squadra1");
        when(votoRepository.findById(votoID)).thenReturn(Optional.of(voto1));
        Optional<VotoEntity> result = votoService.getByVotoID(votoID);
        assertTrue(result.isPresent());
        assertEquals(8.5f, result.get().getPunteggio());
    }

    @Test
    void getByGiudice() {
        when(votoRepository.findByGiudice("giudice1")).thenReturn(Collections.singletonList(voto1));
        List<VotoEntity> result = votoService.getByGiudice("giudice1");
        assertEquals(1, result.size());
    }

    @Test
    void save() {
        when(votoRepository.save(voto1)).thenReturn(voto1);
        VotoEntity result = votoService.save(voto1);
        assertEquals(voto1, result);
    }

    @Test
    void aggiungiVoto() {
        votoService.aggiungiVoto("giudice1", "squadra1", 9.0f);
        verify(votoRepository, times(1)).save(any(VotoEntity.class));
    }
}
