import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordInChiaro = "test"; // la password che vuoi generare
        String hash = encoder.encode(passwordInChiaro);
        System.out.println("Password hashed: " + hash);
    }
}
