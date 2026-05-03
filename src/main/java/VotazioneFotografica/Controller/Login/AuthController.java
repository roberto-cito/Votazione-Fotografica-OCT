package VotazioneFotografica.Controller.Login;

import VotazioneFotografica.Model.ControlFunction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.security.Principal;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String loginPage(Principal principal) throws IOException {
        if(principal==null) return "index";
        else return "redirect:/home";
    }
}