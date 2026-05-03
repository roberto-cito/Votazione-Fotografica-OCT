package VotazioneFotografica.Controller.Squadra;

import VotazioneFotografica.Model.ControlFunction;
import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SquadraHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControlFunction controlFunction;

    @MockBean
    private SquadraService squadraService;

    private SquadraEntity squadra;

    @BeforeEach
    void setUp() {
        squadra = new SquadraEntity("team1", "Team One", "Istituto X");
    }

    @Test
    @WithMockUser(username = "team1", roles = {"SQUADRA"})
    void testHomePageBlocked() throws Exception {
        when(squadraService.getByName("team1")).thenReturn(Optional.of(squadra));
        when(controlFunction.getControl()).thenReturn(0);

        mockMvc.perform(get("/squadra"))
                .andExpect(status().isOk())
                .andExpect(view().name("squadra/attesa"));
    }

    @Test
    @WithMockUser(username = "team1", roles = {"SQUADRA"})
    void testHomePageUploadSbloccatoToUpload() throws Exception {
        squadra.setHas_uploaded(false);
        when(squadraService.getByName("team1")).thenReturn(Optional.of(squadra));
        when(controlFunction.getControl()).thenReturn(1);

        mockMvc.perform(get("/squadra"))
                .andExpect(status().isOk())
                .andExpect(view().name("squadra/toUpload"));
    }

    @Test
    @WithMockUser(username = "team1", roles = {"SQUADRA"})
    void testHomePageUploadSbloccatoHasUploaded() throws Exception {
        squadra.setHas_uploaded(true);
        squadra.setPath_foto("path/to/foto.jpg");
        when(squadraService.getByName("team1")).thenReturn(Optional.of(squadra));
        when(controlFunction.getControl()).thenReturn(1);

        mockMvc.perform(get("/squadra"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("path", "path/to/foto.jpg"))
                .andExpect(view().name("squadra/hasUploaded"));
    }

    @Test
    @WithMockUser(username = "team1", roles = {"SQUADRA"})
    void testHomePageVotoSbloccatoToVote() throws Exception {
        squadra.setHas_voted(false);
        when(squadraService.getByName("team1")).thenReturn(Optional.of(squadra));
        when(controlFunction.getControl()).thenReturn(2);

        mockMvc.perform(get("/squadra"))
                .andExpect(status().isOk())
                .andExpect(view().name("squadra/toVote"));
    }

    @Test
    @WithMockUser(username = "team1", roles = {"SQUADRA"})
    void testHomePageVotoSbloccatoHasVoted() throws Exception {
        squadra.setHas_voted(true);
        when(squadraService.getByName("team1")).thenReturn(Optional.of(squadra));
        when(controlFunction.getControl()).thenReturn(2);

        mockMvc.perform(get("/squadra"))
                .andExpect(status().isOk())
                .andExpect(view().name("squadra/hasVoted"));
    }
}
