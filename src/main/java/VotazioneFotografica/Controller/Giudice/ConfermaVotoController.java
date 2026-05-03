package VotazioneFotografica.Controller.Giudice;

import VotazioneFotografica.Model.Squadre.SquadraRepository;
import VotazioneFotografica.Model.Voti.VotoRepository;
import VotazioneFotografica.Model.Voti.VotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ConfermaVotoController {
    private final VotoService votoService;

    public ConfermaVotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @PostMapping("/confermavoto")
    public String ConfermaVoto(@RequestParam("squadra") String squadra, @RequestParam("voto") float voto, Principal principal, Model model) {
        if(!votoService.hasVoted(principal.getName(),squadra)) {
            votoService.aggiungiVoto(principal.getName(),squadra,voto);
        }
        return "redirect:/giudice?votoCaricato";
    }
}
