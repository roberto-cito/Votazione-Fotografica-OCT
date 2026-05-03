package VotazioneFotografica.Controller.Squadra;

import VotazioneFotografica.Model.ControlFunction;
import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Voti.VotoService;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SquadraPaginaVotoController.class)
public class SquadraPaginaVotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControlFunction controlFunction;

    @MockBean
    private SquadraService squadraService;

    @MockBean
    private VotoService votoService;

    @MockBean
    private VotoSquadraService votoSquadraService;

    @Test
    @WithMockUser(username = "team1", roles = "SQUADRA")
    public void testMostraGalleriaSuccess() throws Exception {
        // Constructor: String squadra, String realName, String istituto
        SquadraEntity me = new SquadraEntity("team1", "Real Team 1", "School 1");
        when(squadraService.getByName("team1")).thenReturn(Optional.of(me));
        when(squadraService.getAllWhoUploadedAndItsNotMe(me)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/votaFoto"))
                .andExpect(status().isOk())
                .andExpect(view().name("squadra/votaFoto"))
                .andExpect(model().attributeExists("squadre"));
    }

    @Test
    @WithMockUser(username = "team1", roles = "SQUADRA")
    public void testConfermaVotoErrorSelf() throws Exception {
        mockMvc.perform(post("/ConfermaVoto")
                        .with(csrf())
                        .param("fotoSelezionata", "team1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/votaFoto?errorSelf"));
    }

    @Test
    @WithMockUser(username = "team1", roles = "SQUADRA")
    public void testConfermaVotoErrorIstituto() throws Exception {
        // Constructor: String squadra, String realName, String istituto
        SquadraEntity me = new SquadraEntity("team1", "Real Team 1", "School 1");
        when(squadraService.getByName("team1")).thenReturn(Optional.of(me));
        when(squadraService.isMyIstituto(me, "team2")).thenReturn(true);

        mockMvc.perform(post("/ConfermaVoto")
                        .with(csrf())
                        .param("fotoSelezionata", "team2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/votaFoto?errorIstituto"));
    }
}
