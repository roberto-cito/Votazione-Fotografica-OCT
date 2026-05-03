package VotazioneFotografica;

import VotazioneFotografica.Model.ControlFunction;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

@SpringBootApplication
public class VotazioneFotograficaApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(VotazioneFotograficaApplication.class, args);
    }

}
