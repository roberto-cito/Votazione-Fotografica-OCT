
package VotazioneFotografica.Controller.Manutenzione;

import VotazioneFotografica.Model.ControlFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class MaintenanceController {
    private final ControlFunction controlFunction;

    @Value("${maintenance.password}")
    private String pass;

    public MaintenanceController(ControlFunction controlFunction) {
        this.controlFunction = controlFunction;
    }

    @GetMapping("/maintenance")
    public String maintenance() throws IOException {
        if(controlFunction.isControl2()) return "maintenance";
        else return "redirect:/login";
    }

    @PostMapping("/IfYouKnowThisURLShootYourself")
    public String controlFunction(@RequestParam("password") String password) throws IOException {
        if(password.equals(pass)) {
            controlFunction.setControl2();
            if(controlFunction.isControl2()) {
                return "maintenance";
            }
            else {
                return "redirect:/login";
            }
        }
        else  {
            return "maintenance";
        }
    }
}