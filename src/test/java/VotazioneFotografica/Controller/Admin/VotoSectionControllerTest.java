package VotazioneFotografica.Controller.Admin;

import VotazioneFotografica.Model.ControlFunction;
import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Users.UserEntity;
import VotazioneFotografica.Model.Voti.VotoEntity;
import VotazioneFotografica.Model.Voti.VotoService;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraEntity;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VotoSectionController.class)
public class VotoSectionControllerTest {

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

    @MockBean
    private VotoSquadraService votoSquadraService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testVotoPageOptimized() throws Exception {
        UserEntity giudice1 = new UserEntity("giudice1", "pass", "GIUDICE");
        List<UserEntity> giudici = Arrays.asList(giudice1);
        
        // Constructor: String squadra, String realName, String istituto
        SquadraEntity squadra1 = new SquadraEntity("team1", "Real Team 1", "School 1");
        List<SquadraEntity> squadre = Arrays.asList(squadra1);
        
        VotoEntity votoGiudice = new VotoEntity("giudice1", "team1", 8.5f);
        List<VotoEntity> allVotiGiudici = Arrays.asList(votoGiudice);
        
        VotoSquadraEntity votoSquadra = new VotoSquadraEntity("team2", "team1");
        List<VotoSquadraEntity> allVotiSquadre = Arrays.asList(votoSquadra);

        when(customUserDetailsService.GetAllGiudici()).thenReturn(giudici);
        when(squadraService.getAll()).thenReturn(squadre);
        when(votoService.getAll()).thenReturn(allVotiGiudici);
        when(votoSquadraService.getAllVoti()).thenReturn(allVotiSquadre);

        mockMvc.perform(get("/admin/vote"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/votopage"))
                .andExpect(model().attributeExists("squadre"))
                .andExpect(model().attributeExists("giudici"))
                .andExpect(model().attributeExists("votiSquadre"))
                .andExpect(model().attributeExists("maxVotiRicevuti"))
                .andExpect(model().attributeExists("conteggio"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testVotoPageSorting() throws Exception {
        UserEntity g1 = new UserEntity("g1", "pass", "GIUDICE");
        UserEntity g2 = new UserEntity("g2", "pass", "GIUDICE");
        
        // Team A: one high vote (8), but only one. Sum=8, Avg=8 (if we count only present votes) 
        // Wait, ControlSquadra initializes missing votes to 0.
        // Team A: g1=8, g2=0. Sum=8, Avg=4
        // Team B: g1=5, g2=5. Sum=10, Avg=5
        // Team B should be first.
        
        SquadraEntity sa = new SquadraEntity("teama", "Team A", "Inst A");
        SquadraEntity sb = new SquadraEntity("teamb", "Team B", "Inst B");
        
        VotoEntity v1a = new VotoEntity("g1", "teama", 8.0f);
        VotoEntity v1b = new VotoEntity("g1", "teamb", 5.0f);
        VotoEntity v2b = new VotoEntity("g2", "teamb", 5.0f);

        when(customUserDetailsService.GetAllGiudici()).thenReturn(Arrays.asList(g1, g2));
        when(squadraService.getAll()).thenReturn(Arrays.asList(sa, sb));
        when(votoService.getAll()).thenReturn(Arrays.asList(v1a, v1b, v2b));
        when(votoSquadraService.getAllVoti()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/admin/vote"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("squadre", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(model().attribute("squadre", org.hamcrest.Matchers.contains(
                        org.hamcrest.Matchers.hasProperty("username", org.hamcrest.Matchers.is("teamb")),
                        org.hamcrest.Matchers.hasProperty("username", org.hamcrest.Matchers.is("teama"))
                )));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetClassificaSuccess() throws Exception {
        UserEntity giudice1 = new UserEntity("giudice1", "pass", "GIUDICE");
        SquadraEntity squadra1 = new SquadraEntity("team1", "Real Team 1", "School 1");
        VotoEntity votoGiudice = new VotoEntity("giudice1", "team1", 8.5f);
        VotoSquadraEntity votoSquadra = new VotoSquadraEntity("team2", "team1");

        when(customUserDetailsService.GetAllGiudici()).thenReturn(Arrays.asList(giudice1));
        when(squadraService.getAll()).thenReturn(Arrays.asList(squadra1));
        when(votoService.getAll()).thenReturn(Arrays.asList(votoGiudice));
        when(votoSquadraService.getAllVoti()).thenReturn(Arrays.asList(votoSquadra));

        String apiCode = "YOUR_API_KEY_HERE";

        mockMvc.perform(get("/admin/getclassifica").param("apicode", apiCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].username").value("team1"))
                .andExpect(jsonPath("$[0].realname").value("Real Team 1"))
                .andExpect(jsonPath("$[0].totaleDaSquadre").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetClassificaWrongApiCode() throws Exception {
        mockMvc.perform(get("/admin/getclassifica").param("apicode", "wrongcode"))
                .andExpect(status().isBadRequest());
    }
}
