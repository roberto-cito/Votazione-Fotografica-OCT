package VotazioneFotografica.Controller.Squadra;

import VotazioneFotografica.Model.ControlFunction;
import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraRepository;
import VotazioneFotografica.Model.Squadre.SquadraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Controller
public class SquadraHomeController {

    private final SquadraService squadraService;
    private final ControlFunction controlFunction;

    public SquadraHomeController(SquadraService squadraService, ControlFunction controlFunction) {
        this.squadraService=squadraService;
        this.controlFunction=controlFunction;
    }

    @GetMapping("/squadra")
    public String HomePage(Principal principal, Model model) throws IOException {
        String username=principal.getName();
        Optional<SquadraEntity> temp=squadraService.getByName(username);
        SquadraEntity squadra;
        if(temp.isPresent()) squadra=temp.get();
        else {
            model.addAttribute("errore","Non è stata trovata la corrispondenza da login a squadra");
            return "errore";
        }
        if(controlFunction.getControl()==1) {
            if(squadra.isHas_uploaded()) {
                model.addAttribute("path",squadra.getPath_foto());
                return "squadra/hasUploaded";
            }
            else {
                model.addAttribute("squadra",squadra.getRealName());
                return "squadra/toUpload";
            }
        }
        else if(controlFunction.getControl()==2) {
            if(!squadra.isHas_voted()) {
                return "squadra/toVote";
            }
            else {
                return "squadra/hasVoted";
            }
        }
        else {
            return "squadra/attesa";
        }
    }
}
