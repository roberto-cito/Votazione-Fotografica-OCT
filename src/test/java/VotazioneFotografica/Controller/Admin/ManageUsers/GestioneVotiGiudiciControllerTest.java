package VotazioneFotografica.Controller.Admin.ManageUsers;

import VotazioneFotografica.Model.ControlFunction;
import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Voti.VotoEntity;
import VotazioneFotografica.Model.Voti.VotoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GestioneVotiGiudiciController.class)
public class GestioneVotiGiudiciControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControlFunction controlFunction;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private SquadraService squadraService;

    @MockBean
    private VotoService votoService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGestioneGiudici() throws Exception {
        SquadraEntity sq1 = new SquadraEntity("sq1", "Squadra 1", "Ist 1");
        VotoEntity v1 = new VotoEntity("giudice1", "sq1", 10.0f);

        when(squadraService.getAll()).thenReturn(Arrays.asList(sq1));
        when(votoService.getAllVotiBySquadra("sq1")).thenReturn(Arrays.asList(v1));

        mockMvc.perform(get("/admin/gestione-giudici"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/gestioneVotiGiudici"))
                .andExpect(model().attributeExists("voti"));

        verify(squadraService, times(1)).getAll();
        verify(votoService, times(1)).getAllVotiBySquadra("sq1");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoveVotoSuccess() throws Exception {
        VotoEntity voto = new VotoEntity("giudice1", "sq1", 10.0f);
        when(votoService.getByGiudiceAndSquadra("giudice1", "sq1")).thenReturn(Optional.of(voto));

        mockMvc.perform(post("/admin/remove-voto-giudice")
                        .with(csrf())
                        .param("giudice", "giudice1")
                        .param("squadra", "sq1"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/gestioneVotiGiudici"))
                .andExpect(model().attribute("votoRimosso", true));

        verify(votoService, times(1)).removeVoto(voto);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoveVotoNotFound() throws Exception {
        when(votoService.getByGiudiceAndSquadra("giudice1", "sq1")).thenReturn(Optional.empty());

        mockMvc.perform(post("/admin/remove-voto-giudice")
                        .with(csrf())
                        .param("giudice", "giudice1")
                        .param("squadra", "sq1"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/gestioneVotiGiudici"))
                .andExpect(model().attribute("ErrorRemoveVoto", true));

        verify(votoService, never()).removeVoto(any(VotoEntity.class));
    }
}
