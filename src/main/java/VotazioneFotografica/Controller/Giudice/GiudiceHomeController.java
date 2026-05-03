package VotazioneFotografica.Controller.Giudice;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraRepository;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Voti.VotoEntity;
import VotazioneFotografica.Model.Voti.VotoRepository;
import VotazioneFotografica.Model.Voti.VotoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.*;

@Controller
public class GiudiceHomeController {
    private final VotoService votoService;
    private final SquadraService squadraService;

    public GiudiceHomeController(VotoService votoService, SquadraService squadraService) {
        this.votoService = votoService;
        this.squadraService = squadraService;
    }

    @GetMapping("/giudice")
    public String HomePage(Principal principal, Model model) {
        List<VotoEntity> voti=votoService.getByGiudice(principal.getName());
        List<SquadraEntity> squadre=squadraService.getAllWhoUploaded();
        for(VotoEntity voto:voti) {
            Iterator<SquadraEntity> it=squadre.iterator();
            while (it.hasNext()) {
                SquadraEntity temp=it.next();
                if(temp.getSquadra().equals(voto.getSquadra())) it.remove();
            }
        }
        Map<String,String> paths=new HashMap<>();
        for(SquadraEntity squadra:squadre) {
            paths.put(squadra.getSquadra(),squadra.getPath_foto());
        }
        model.addAttribute("giudiceNome",principal.getName());
        model.addAttribute("voti",voti);
        model.addAttribute("squadre",squadre);
        model.addAttribute("paths",paths);
        return "giudice/listafoto";
    }

    @GetMapping("/votafoto")
    public String votaFoto(@RequestParam String squadra, Principal principal, Model model) {
        Optional<SquadraEntity> squadraEntityOptional=squadraService.getByName(squadra);
        if(squadraEntityOptional.isEmpty()) {
            return "redirect:/giudice?errorSquadra";
        }
        List<VotoEntity> voti=votoService.getByGiudice(principal.getName());
        model.addAttribute("squadra",squadraEntityOptional.get());
        model.addAttribute("voti",voti);
        return "giudice/voto";
    }

    @PostMapping("/giudice")
    public String InviaVoto(@RequestParam float voto,  @RequestParam String squadra, Principal principal, Model model) {
        Optional<SquadraEntity> squadraEntityOptional=squadraService.getByName(squadra);
        if(squadraEntityOptional.isEmpty()) {
            return "redirect:/giudice?errorSquadra";
        }
        SquadraEntity squadraEntity=squadraEntityOptional.get();
        String realName=squadraEntity.getRealName();
        String path=squadraEntity.getPath_foto();
        model.addAttribute("voto",voto);
        model.addAttribute("squadra",squadra);
        model.addAttribute("realName",realName);
        model.addAttribute("path",path);
        return "giudice/confermaVoto";
    }
}
