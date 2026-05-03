package VotazioneFotografica.DBPopulator;

import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Users.PasswordUtility;
import VotazioneFotografica.Model.Users.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class AdminPopulator {
    private final CustomUserDetailsService customUserDetailsService;

    public AdminPopulator(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService=customUserDetailsService;
    }

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            try {
                customUserDetailsService.loadUserByUsername("rcito");
                System.out.println("Admin già esistente");
            } catch (UsernameNotFoundException e) {
                UserEntity user=new UserEntity("test",PasswordUtility.hashPassword("testtest"), "ADMIN");
                customUserDetailsService.save(user);
                System.out.println("Admin aggiunto");
            }
        };
    }
}
