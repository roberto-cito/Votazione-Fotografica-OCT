package VotazioneFotografica.Controller.Squadra;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraRepository;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Voti.VotoService;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraEntity;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraRepository;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class SquadraPaginaVotoController {
    private final SquadraService squadraService;
    private final VotoService votoService;
    private final VotoSquadraService votoSquadraService;

    public SquadraPaginaVotoController(SquadraService squadraService, VotoService votoService, VotoSquadraService votoSquadraService) {
        this.squadraService = squadraService;
        this.votoService = votoService;
        this.votoSquadraService = votoSquadraService;
    }

    @GetMapping("/votaFoto")
    public String mostraGalleria(Principal principal, Model model) {
        String me = principal.getName();
        Optional<SquadraEntity> myOpt = squadraService.getByName(me);
        if (myOpt.isEmpty()) {
            return "redirect:/errore";
        }
        SquadraEntity my = myOpt.get();
        model.addAttribute("squadre", squadraService.getAllWhoUploadedAndItsNotMe(my));
        return "squadra/votaFoto";
    }

    @PostMapping("/ConfermaVoto")
    public String confermaVoto(@RequestParam("fotoSelezionata") String selezionata, Principal principal) {
        String me = principal.getName();
        if (me.equals(selezionata)) {
            return "redirect:/votaFoto?errorSelf";
        }
        Optional<SquadraEntity> myOpt = squadraService.getByName(me);
        if (myOpt.isEmpty()) {
            return "redirect:/errore";
        }
        SquadraEntity my = myOpt.get();
        if(my.getSquadra().equals(selezionata)) {
            return "redirect:/votaFoto?errorSelf";
        }
        if(squadraService.isMyIstituto(my,selezionata)) {
            return "redirect:/votaFoto?errorIstituto";
        }
        if (!my.isHas_voted()) {
            my.setHas_voted(true);
            VotoSquadraEntity votoSquadraEntity=new VotoSquadraEntity(principal.getName(),selezionata);
            squadraService.save(my);
            votoSquadraService.save(votoSquadraEntity);
        }
        return "redirect:/squadra";
    }
}