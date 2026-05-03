package VotazioneFotografica.Controller.Login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    @GetMapping("/home")
    public String HomePage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()  // prendi il primo ruolo
                .orElse("USER");

        switch (role) {
            case "ROLE_SQUADRA":
                return "redirect:/squadra";
            case "ROLE_GIUDICE":
                return "redirect:/giudice";
            case "ROLE_ADMIN":
                return "redirect:/admin";
            default:
                return "redirect:/login";
        }
    }
}
