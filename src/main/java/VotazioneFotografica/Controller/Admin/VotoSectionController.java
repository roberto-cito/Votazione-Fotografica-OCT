package VotazioneFotografica.Controller.Admin;

import VotazioneFotografica.Model.ControlSquadra;
import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraRepository;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Users.UserEntity;
import VotazioneFotografica.Model.Users.UserRepository;
import VotazioneFotografica.Model.Voti.VotoEntity;
import VotazioneFotografica.Model.Voti.VotoRepository;
import VotazioneFotografica.Model.Voti.VotoService;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraEntity;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraRepository;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VotoSectionController {
    private final CustomUserDetailsService customUserDetailsService;
    private final SquadraService squadraService;
    private final VotoService votoService;
    private final VotoSquadraService votoSquadraService;

    public VotoSectionController(CustomUserDetailsService customUserDetailsService, SquadraService squadraService, VotoService votoService, VotoSquadraService votoSquadraService) {
        this.customUserDetailsService=customUserDetailsService;
        this.squadraService=squadraService;
        this.votoService=votoService;
        this.votoSquadraService=votoSquadraService;
    }


    @GetMapping("/admin/vote")
    public String VotoPage(Model model) {
        List<UserEntity> giudici=customUserDetailsService.GetAllGiudici();
        List<SquadraEntity> allSquadre = squadraService.getAll();
        List<VotoEntity> allVotiGiudici = votoService.getAll();
        List<VotoSquadraEntity> allVotiSquadre = votoSquadraService.getAllVoti();

        // Mappa per i voti dei giudici: Map<squadra, Map<giudice, punteggio>>
        Map<String, Map<String, Float>> mappedVotiGiudici = allVotiGiudici.stream()
                .collect(Collectors.groupingBy(VotoEntity::getSquadra,
                        Collectors.toMap(VotoEntity::getGiudice, VotoEntity::getPunteggio, (v1, v2) -> v1)));

        // Mappa per i voti delle squadre: Map<squadraVotata, count>
        Map<String, Long> mappedVotiSquadre = allVotiSquadre.stream()
                .collect(Collectors.groupingBy(VotoSquadraEntity::getSquadraVotata, Collectors.counting()));

        List<ControlSquadra> squadre=new ArrayList<>();
        for(SquadraEntity squadra : allSquadre) {
            HashMap<String,Float> votiGiudici=new HashMap<>();
            Map<String, Float> votesFound = mappedVotiGiudici.getOrDefault(squadra.getSquadra(), new HashMap<>());

            for(UserEntity giudice : giudici) {
                votiGiudici.put(giudice.getUsername(), votesFound.getOrDefault(giudice.getUsername(), 0f));
            }

            int countVotiSquadre = mappedVotiSquadre.getOrDefault(squadra.getSquadra(), 0L).intValue();
            squadre.add(new ControlSquadra(squadra.getSquadra(),squadra.getRealName(),squadra.isHas_uploaded(),squadra.getPath_foto(),squadra.isHas_voted(),squadra.getIstituto(),votiGiudici,countVotiSquadre));
        }
        squadre.sort(Comparator.comparingDouble(ControlSquadra::getMediaGiudici).reversed());
        List<VotoSquadraEntity> votoSquadraEntities=allVotiSquadre;
        /* Controllo di debug
        for(ControlSquadra temp:squadre) {
            System.out.println(temp);
        }*/
        int maxVoti=0, conteggio=0;
        for(ControlSquadra temp:squadre) {
            if(maxVoti<temp.getTotaleDaSquadre()) maxVoti=temp.getTotaleDaSquadre();
        }
        for(ControlSquadra temp:squadre) {
            if(temp.getTotaleDaSquadre()==maxVoti) conteggio++;
        }
        model.addAttribute("squadre",squadre);
        model.addAttribute("giudici",giudici);
        model.addAttribute("votiSquadre",votoSquadraEntities);
        model.addAttribute("maxVotiRicevuti",maxVoti);
        model.addAttribute("conteggio",conteggio);
        return "Admin/votopage";
    }

    @GetMapping("/visualizzaFoto")
    public String VisualizzaFoto(@RequestParam("squadra") String squadra, Model model) {
        Optional<SquadraEntity> temp=squadraService.getByName(squadra);
        if(temp.isPresent()) {
            SquadraEntity squadra1=temp.get();
            model.addAttribute("nome",squadra1.getRealName());
            model.addAttribute("path",squadra1.getPath_foto());
            model.addAttribute("squadra", squadra1.getSquadra());
            return "Admin/visualizzaFoto";
        }
        else {
            return "errore";
        }
    }

    @PostMapping("/admin/rimuovi-foto")
    public String rimuoviFoto(@RequestParam("squadra") String squadra) {
        Optional<SquadraEntity> temp=squadraService.getByName(squadra);
        if(temp.isPresent()) squadraService.removeFoto(squadra);
        return "redirect:/admin/vote";
    }

    @GetMapping("/admin/getclassifica")
    @ResponseBody
    public ResponseEntity<List<ControlSquadra>> getClassifica(@RequestParam("apicode") String apicode) {
        String API_CODE = "YOUR_API_KEY_HERE";
        if(apicode.equals(API_CODE)) {
            List<UserEntity> giudici=customUserDetailsService.GetAllGiudici();
            List<SquadraEntity> allSquadre = squadraService.getAll();
            List<VotoEntity> allVotiGiudici = votoService.getAll();
            List<VotoSquadraEntity> allVotiSquadre = votoSquadraService.getAllVoti();

            // Mappa per i voti dei giudici: Map<squadra, Map<giudice, punteggio>>
            Map<String, Map<String, Float>> mappedVotiGiudici = allVotiGiudici.stream()
                    .collect(Collectors.groupingBy(VotoEntity::getSquadra,
                            Collectors.toMap(VotoEntity::getGiudice, VotoEntity::getPunteggio, (v1, v2) -> v1)));

            // Mappa per i voti delle squadre: Map<squadraVotata, count>
            Map<String, Long> mappedVotiSquadre = allVotiSquadre.stream()
                    .collect(Collectors.groupingBy(VotoSquadraEntity::getSquadraVotata, Collectors.counting()));

            List<ControlSquadra> squadre=new ArrayList<>();
            for(SquadraEntity squadra : allSquadre) {
                HashMap<String,Float> votiGiudici=new HashMap<>();
                Map<String, Float> votesFound = mappedVotiGiudici.getOrDefault(squadra.getSquadra(), new HashMap<>());

                for(UserEntity giudice : giudici) {
                    votiGiudici.put(giudice.getUsername(), votesFound.getOrDefault(giudice.getUsername(), 0f));
                }

                int countVotiSquadre = mappedVotiSquadre.getOrDefault(squadra.getSquadra(), 0L).intValue();
                squadre.add(new ControlSquadra(squadra.getSquadra(),squadra.getRealName(),squadra.isHas_uploaded(),squadra.getPath_foto(),squadra.isHas_voted(),squadra.getIstituto(),votiGiudici,countVotiSquadre));
            }
            squadre.sort(Comparator.comparingDouble(ControlSquadra::getMediaGiudici).reversed());
            return ResponseEntity.ok(squadre);
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/admin/getngiudici")
    @ResponseBody
    public ResponseEntity<Integer> getnGiudici(@RequestParam("apicode") String apicode) {
        String API_CODE = "YOUR_API_KEY_HERE";
        if(apicode.equals(API_CODE)) {
            return ResponseEntity.ok(customUserDetailsService.GetAllGiudici().size());
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }
}