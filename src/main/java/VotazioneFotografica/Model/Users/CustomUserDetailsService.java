package VotazioneFotografica.Model.Users;

import VotazioneFotografica.Model.Voti.VotoRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final VotoRepository votoRepository;

    public CustomUserDetailsService(UserRepository userRepository, VotoRepository votoRepository) {
        this.userRepository = userRepository;
        this.votoRepository = votoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
        return new CustomUserDetails(user);
    }

    public UserEntity loadUser(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
        return user;
    }

    public List<UserEntity> GetAllGiudici() {
        return userRepository.getAllByRole("GIUDICE");
    }

    @Transactional
    public void save(UserEntity user) {
        userRepository.save(user);
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void remove(String username) {
        votoRepository.deleteByGiudice(username);
        userRepository.delete(userRepository.findByUsername(username).orElseThrow());
    }
}