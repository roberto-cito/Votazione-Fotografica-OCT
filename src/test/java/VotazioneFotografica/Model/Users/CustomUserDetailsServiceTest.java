package VotazioneFotografica.Model.Users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setHashPassword(PasswordUtility.hashPassword("password"));
        userEntity.setRole("GIUDICE");
    }

    @Test
    void loadUserByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("unknown");
        });
    }

    @Test
    void getAllGiudici() {
        when(userRepository.getAllByRole("GIUDICE")).thenReturn(Collections.singletonList(userEntity));

        List<UserEntity> result = customUserDetailsService.GetAllGiudici();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }
}
