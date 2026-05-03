package VotazioneFotografica.Controller.Admin.ManageUsers;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Voti.VotoEntity;
import VotazioneFotografica.Model.Voti.VotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
public class GestioneVotiGiudiciController {
    private final SquadraService squadraService;
    private final VotoService votoService;
    private final CustomUserDetailsService customUserDetailsService;

    public GestioneVotiGiudiciController(SquadraService squadraService, VotoService votoService, CustomUserDetailsService customUserDetailsService) {
        this.squadraService = squadraService;
        this.votoService = votoService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/admin/gestione-giudici")
    public String gestioneGiudici(Principal principal, Model model) {
        HashMap<SquadraEntity, List<VotoEntity>> voti=new HashMap<>();
        for(SquadraEntity squadra:squadraService.getAll()) {
            voti.put(squadra,votoService.getAllVotiBySquadra(squadra.getSquadra()));
        }
        model.addAttribute("voti",voti);
        return "Admin/gestioneVotiGiudici";
    }

    @PostMapping("/admin/remove-voto-giudice")
    public String removeVoto(Principal principal, Model model, @RequestParam String giudice, @RequestParam String squadra) {
        Optional<VotoEntity> votoEntityOptional=votoService.getByGiudiceAndSquadra(giudice,squadra);
        if(votoEntityOptional.isPresent()) {
            votoService.removeVoto(votoEntityOptional.get());
            model.addAttribute("votoRimosso",true);
            return "Admin/gestioneVotiGiudici";
        }
        else {
            model.addAttribute("ErrorRemoveVoto",true);
            return "Admin/gestioneVotiGiudici";
        }
    }
}
