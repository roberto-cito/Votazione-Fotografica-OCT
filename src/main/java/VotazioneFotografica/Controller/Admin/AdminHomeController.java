package VotazioneFotografica.Controller.Admin;

import VotazioneFotografica.Model.ControlFunction;
import VotazioneFotografica.Model.Users.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.naming.ldap.Control;
import java.io.IOException;
import java.security.Principal;

@Controller
public class AdminHomeController {
    private final ControlFunction controlFunction;

    public AdminHomeController(ControlFunction controlFunction) {
        this.controlFunction = controlFunction;
    }

    @GetMapping("/admin")
    public String AdminHome(Principal principal, Model model) throws IOException {
        model.addAttribute("utente",principal.getName());
        model.addAttribute("modalitaUtente",controlFunction.getControl());
        return "Admin/homePage";
    }

    @PostMapping("/admin/cambiaModalita")
    public String CambiaModalita(@RequestParam Integer control) throws IOException {
        if(control!=null) controlFunction.setControl(control);
        return "redirect:/admin";
    }
}