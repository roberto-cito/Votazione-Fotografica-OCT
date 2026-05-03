package VotazioneFotografica.Controller.Admin.ManageUsers;

import VotazioneFotografica.Model.Squadre.SquadraEntity;
import VotazioneFotografica.Model.Squadre.SquadraService;
import VotazioneFotografica.Model.Users.CustomUserDetailsService;
import VotazioneFotografica.Model.Users.PasswordUtility;
import VotazioneFotografica.Model.Users.UserEntity;
import VotazioneFotografica.Model.VotoSquadra.VotoSquadraService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.*;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Random;

@Controller
public class GestioneUtentiController {
    private final CustomUserDetailsService customUserDetailsService;
    private final SquadraService squadraService;
    private final SessionRegistry sessionRegistry;
    private final VotoSquadraService votoSquadraService;

    public GestioneUtentiController(CustomUserDetailsService customUserDetailsService, SquadraService squadraService, SessionRegistry sessionRegistry, VotoSquadraService votoSquadraService) {
        this.customUserDetailsService = customUserDetailsService;
        this.squadraService = squadraService;
        this.sessionRegistry = sessionRegistry;
        this.votoSquadraService = votoSquadraService;
    }

    private String generatePassword() { //Generatore password casuale
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@$";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private void populateModel(Model model) {
        model.addAttribute("users", customUserDetailsService.getAllUsers());
        if (!model.containsAttribute("addUserForm")) {
            model.addAttribute("addUserForm", new AddUserForm());
        }
        if (!model.containsAttribute("addSquadraForm")) {
            model.addAttribute("addSquadraForm", new AddSquadraForm());
        }
    }

    @GetMapping("/admin/manage-users")
    public String getPage(Principal principal, Model model) {
        populateModel(model);
        return "Admin/manageUsers";
    }

    @PostMapping("/admin/add-user")
    public String addUser(Principal principal, Model model, @Valid @ModelAttribute AddUserForm form, BindingResult bindingResult) {
        if(!form.getRole().equals("GIUDICE") && !form.getRole().equals("ADMIN")) {
             bindingResult.rejectValue("role","role.notfound","Il ruolo può essere o giudice o admin");
        }
        try {
            customUserDetailsService.loadUserByUsername(form.getUsername());
            bindingResult.rejectValue("username","username.alreadyexists","L'username inserito è già utilizzato da un altro utente");
        } catch (UsernameNotFoundException ue) {
            // Username is available
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("addUserForm", form); // Ensure the form with errors is in the model
            populateModel(model);
            return "Admin/manageUsers";
        }
        String password=generatePassword();
        UserEntity user=new UserEntity(form.getUsername(),PasswordUtility.hashPassword(password),form.getRole());
        customUserDetailsService.save(user);
        model.addAttribute("addedUserEntity", user); // Changed key to avoid conflict
        model.addAttribute("newPassword",password);
        model.addAttribute("AddedUser",true);
        populateModel(model);
        return "Admin/manageUsers";
    }

    @PostMapping("/admin/add-squadra")
    public String addSquadra(Principal principal, Model model, @Valid @ModelAttribute AddSquadraForm form, BindingResult bindingResult) {
        try {
            customUserDetailsService.loadUserByUsername(form.getUsername());
            bindingResult.rejectValue("username","username.alreadyexists","L'username inserito è già utilizzato da un altro utente");
        } catch (UsernameNotFoundException ue) {
            // Username is available
        }
        if(bindingResult.hasErrors()) {
             model.addAttribute("addSquadraForm", form);
             populateModel(model);
             return "Admin/manageUsers";
        }
        String password=generatePassword();
        UserEntity user=new UserEntity(form.getUsername(),PasswordUtility.hashPassword(password),"SQUADRA");
        customUserDetailsService.save(user);
        SquadraEntity squadra=new SquadraEntity(form.getUsername(), form.getRealName(),form.getIstituto());
        squadraService.save(squadra);
        model.addAttribute("addedUserEntity", user);
        model.addAttribute("newPassword",password);
        model.addAttribute("AddedSquadra",true);
        populateModel(model);
        return "Admin/manageUsers";
    }

    @PostMapping("/admin/remove-user")
    public String removeSquadra(Principal principal, Model model, @RequestParam String username) {
        try {
            customUserDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ue) {
            model.addAttribute("ErrorRemoveSquadra",true);
            populateModel(model);
            return "Admin/manageUsers";
        }
        if(principal.getName().equals(username)) {
            model.addAttribute("ErrorRemoveSquadra",true);
            populateModel(model);
            return "Admin/manageUsers";
        }
        UserEntity user=customUserDetailsService.loadUser(username);
        if(user.getRole().equals("SQUADRA")) squadraService.remove(username);
        customUserDetailsService.remove(username);
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object p : allPrincipals) {
            if (p instanceof UserDetails userDetails) {
                if (userDetails.getUsername().equals(username)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(p, false);
                    for (SessionInformation session : sessions) {
                        session.expireNow();
                    }
                }
            }
        }
        model.addAttribute("RemovedSquadra",true);
        populateModel(model);
        return "Admin/manageUsers";
    }

    @PostMapping("/admin/add-squadre")
    public ResponseEntity<byte[]> addSquadre(Principal principal, Model model, @RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();
        try (// Lettura del CSV in ingresso
             Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

             // Scrittura del CSV in uscita (in memoria)
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader("Nome_Utente", "Squadra", "Istituto", "Password")))
            {
                for (CSVRecord record : csvParser) {
                    String nomeReale=record.get(0);
                    String username=nomeReale.replace(" ", "_").toLowerCase();
                    String istituto=record.get(1);
                    if(username.isEmpty() || nomeReale.isEmpty() || istituto.isEmpty()) {
                        return ResponseEntity.status(601).build();
                    }
                    try {
                        customUserDetailsService.loadUserByUsername(username);
                        return ResponseEntity.status(602).build();
                    } catch (UsernameNotFoundException ignored) {}
                    String password=generatePassword();
                    UserEntity user=new UserEntity(username,PasswordUtility.hashPassword(password),"SQUADRA");
                    customUserDetailsService.save(user);
                    SquadraEntity squadra=new SquadraEntity(username,nomeReale,istituto);
                    squadraService.save(squadra);
                    csvPrinter.printRecord(username,nomeReale,istituto,password);
                }
                csvPrinter.flush();
                byte[] csvBytes = out.toByteArray();

                // Configurazione Headers per la risposta
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType("text/csv"));
                headers.setContentDisposition(ContentDisposition.attachment().filename("squadre.csv").build());

                return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/admin/reset-password")
    public String resetPassword(Principal principal, Model model, @RequestParam String username, @RequestParam(required = false) String password) {
        try {
            customUserDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ue) {
            model.addAttribute("ErrorResetPassword",true);
            populateModel(model);
            return "Admin/manageUsers";
        }
        String newPassword;
        if(password!=null && !password.isEmpty()) newPassword=password;
        else newPassword=generatePassword();
        UserEntity user=customUserDetailsService.loadUser(username);
        user.setHashPassword(PasswordUtility.hashPassword(newPassword));
        customUserDetailsService.save(user);
        model.addAttribute("newPassword",newPassword);
        model.addAttribute("ResetPassword",true);
        populateModel(model);
        return "Admin/manageUsers";
    }

    @PostMapping("/admin/delete-all")
    public String deleteAll() {
        squadraService.removeAll();
        List<UserEntity> allUsers = customUserDetailsService.getAllUsers();
        for (UserEntity user : allUsers) {
            if ("SQUADRA".equals(user.getRole())) {
                customUserDetailsService.remove(user.getUsername());
                List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
                for (Object p : allPrincipals) {
                    if (p instanceof UserDetails userDetails) {
                        if (userDetails.getUsername().equals(user.getUsername())) {
                            List<SessionInformation> sessions = sessionRegistry.getAllSessions(p, false);
                            for (SessionInformation session : sessions) {
                                session.expireNow();
                            }
                        }
                    }
                }
            }
        }
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/admin/remove-voto")
    public String removeVoto(Principal principal, Model model, @RequestParam String username) {
        try {
            customUserDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ue) {
            model.addAttribute("ErrorRemoveVoto",true);
            populateModel(model);
            return "Admin/manageUsers";
        }
        UserEntity user=customUserDetailsService.loadUser(username);
        if(user.getRole().equals("SQUADRA")) {
            SquadraEntity squadra=squadraService.getByName(username).get();
            squadra.setHas_voted(false);
            votoSquadraService.removeBySquadra(username);
            squadraService.save(squadra);
            model.addAttribute("RemovedVoto",true);
            populateModel(model);
            return "Admin/manageUsers";
        }
        else {
            model.addAttribute("ErrorRemoveVoto",true);
            populateModel(model);
            return "Admin/manageUsers";
        }
    }
}