package VotazioneFotografica.Controller.Admin.ManageUsers;

import VotazioneFotografica.Model.ControlFunction;
import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Users.UserEntity;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GestioneUtentiController.class)
public class GestioneUtentiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControlFunction controlFunction;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private SquadraService squadraService;

    @MockBean
    private SessionRegistry sessionRegistry;

    @MockBean
    private VotoSquadraService votoSquadraService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetPage() throws Exception {
        when(customUserDetailsService.getAllUsers()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/manage-users"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("addUserForm"))
                .andExpect(model().attributeExists("addSquadraForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUserSuccess() throws Exception {
        when(customUserDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException(""));

        mockMvc.perform(post("/admin/add-user")
                        .with(csrf())
                        .param("username", "newAdmin")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().attribute("AddedUser", true))
                .andExpect(model().attributeExists("newPassword"));

        verify(customUserDetailsService, times(1)).save(any(UserEntity.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUserInvalidRole() throws Exception {
        mockMvc.perform(post("/admin/add-user")
                        .with(csrf())
                        .param("username", "newAdmin")
                        .param("role", "INVALID"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddSquadraSuccess() throws Exception {
        when(customUserDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException(""));

        mockMvc.perform(post("/admin/add-squadra")
                        .with(csrf())
                        .param("username", "team1")
                        .param("realName", "Real Team 1")
                        .param("istituto", "School 1"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().attribute("AddedSquadra", true))
                .andExpect(model().attributeExists("newPassword"));

        verify(customUserDetailsService, times(1)).save(any(UserEntity.class));
        verify(squadraService, times(1)).save(any(SquadraEntity.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testRemoveUser() throws Exception {
        UserEntity user = new UserEntity("testUser", "pass", "SQUADRA");
        when(customUserDetailsService.loadUser("testUser")).thenReturn(user);
        when(sessionRegistry.getAllPrincipals()).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/admin/remove-user")
                        .with(csrf())
                        .param("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().attribute("RemovedSquadra", true));

        verify(squadraService, times(1)).remove("testUser");
        verify(customUserDetailsService, times(1)).remove("testUser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddSquadreCSV() throws Exception {
        String csvContent = "Nome_Utente,Squadra,Istituto\n" +
                "user1,Team 1,School 1\n" +
                "user2,Team 2,School 2";
        MockMultipartFile file = new MockMultipartFile("file", "squadre.csv", "text/csv", csvContent.getBytes());

        when(customUserDetailsService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException(""));

        mockMvc.perform(multipart("/admin/add-squadre")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"squadre.csv\""));

        verify(customUserDetailsService, times(2)).save(any(UserEntity.class));
        verify(squadraService, times(2)).save(any(SquadraEntity.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testResetPasswordSuccess() throws Exception {
        UserEntity user = new UserEntity("testUser", "pass", "SQUADRA");
        when(customUserDetailsService.loadUser("testUser")).thenReturn(user);
        
        mockMvc.perform(post("/admin/reset-password")
                        .with(csrf())
                        .param("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().attribute("ResetPassword", true))
                .andExpect(model().attributeExists("newPassword"));

        verify(customUserDetailsService, times(1)).save(any(UserEntity.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testResetPasswordUserNotFound() throws Exception {
        when(customUserDetailsService.loadUserByUsername("unknown")).thenThrow(new UsernameNotFoundException(""));

        mockMvc.perform(post("/admin/reset-password")
                        .with(csrf())
                        .param("username", "unknown"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().attribute("ErrorResetPassword", true));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    public void testRemoveSelfUser() throws Exception {
        mockMvc.perform(post("/admin/remove-user")
                        .with(csrf())
                        .param("username", "adminUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/manageUsers"))
                .andExpect(model().attribute("ErrorRemoveSquadra", true));
    }
}
