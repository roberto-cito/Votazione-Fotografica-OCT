package VotazioneFotografica.Controller.Admin;

import VotazioneFotografica.Model.ControlFunction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControlFunction controlFunction;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminHome() throws Exception {
        when(controlFunction.getControl()).thenReturn(1);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("utente", "admin"))
                .andExpect(model().attribute("modalitaUtente", 1))
                .andExpect(view().name("Admin/homePage"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCambiaModalita() throws Exception {
        mockMvc.perform(post("/admin/cambiaModalita")
                        .param("control", "2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(controlFunction, times(1)).setControl(2);
    }
}
