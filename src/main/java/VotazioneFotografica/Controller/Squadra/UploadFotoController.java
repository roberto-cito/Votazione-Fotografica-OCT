package VotazioneFotografica.Controller.Squadra;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraRepository;
import VotazioneFotografica.Model.Squadre.SquadraService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Optional;

@Controller
public class UploadFotoController {
    private final SquadraService squadraService;
    private final String[] supportedFormats={".png",".jpg",".jpeg",".raw",".heif",".heic",".PNG",".JPG",".JPEG",".RAW",".HEIF",".HEIC"};

    public UploadFotoController(SquadraService squadraService) {
        this.squadraService = squadraService;
    }

    @PostMapping("/SalvaFoto")
    public String SalvaFoto(@RequestParam("foto") MultipartFile file, Principal principal, Model model) throws IOException {
        if(file.isEmpty() || file.getOriginalFilename()==null) {
            return "redirect:/hasUploaded?error";
        }
        String username=principal.getName();
        Optional<SquadraEntity> temp=squadraService.getByName(username);
        SquadraEntity squadra=temp.get();
        if(squadra.isHas_uploaded()) {
            return "squadra/hasUploaded";
        }

        String uploadDir="uploads/";
        Path uploadPath=Paths.get(uploadDir);
        if(!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        String filename=username+"_"+ StringUtils.cleanPath(file.getOriginalFilename());
        boolean isSupported=false;
        for(String format:supportedFormats) {
            if(file.getOriginalFilename().endsWith(format)) isSupported=true;
        }
        if(!isSupported) {
            return "redirect:/hasUploaded?error";
        }
        Path filePath=uploadPath.resolve(filename);
        Files.write(filePath,file.getBytes());


        squadra.setHas_uploaded(true);
        squadra.setPath_foto(filePath.toString());
        squadraService.save(squadra);

        model.addAttribute("path","uploads/"+filename);
        return "squadra/hasUploaded";
    }
}
