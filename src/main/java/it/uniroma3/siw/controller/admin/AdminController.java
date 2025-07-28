
package it.uniroma3.siw.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import it.uniroma3.siw.service.*;
import org.springframework.web.bind.annotation.PostMapping;





@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    AdminController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/account")
    public String getAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();

        if (credentials == null) {
            return "admin/indexAdmin"; // oppure una pagina di errore più adatta
        }

        model.addAttribute("credentials", credentials);

        if(Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
            List<Credentials> utenti = (List<Credentials>) credentialsService.findAll();
            //rimuovi l'admin dalla lista degli utenti
            utenti.removeIf(c -> c.getId().equals(credentials.getId()));
            model.addAttribute("utenti", utenti);
        }

        return "admin/adminAccount";
    }
    
    @GetMapping("/modificaAccount")
    public String getModificaAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();
        model.addAttribute("credentials", credentials);
        return "admin/adminModificaAccount";
    }
    

    @PostMapping("/account")
    public String updateAccount(@ModelAttribute("credentials") Credentials updatedCredentials, Model model) {
        Credentials current = credentialsService.getCurrentCredentials();

        if (current == null || !updatedCredentials.getId().equals(current.getId())) {
            model.addAttribute("error", "Accesso non autorizzato");
            return "redirect:/admin/account";
        }
        userService.updateUser(updatedCredentials.getUser());
        credentialsService.updateCredentials(updatedCredentials);
        // 🔄 Riautentica l'utente con le nuove credenziali
        credentialsService.autoLogin(updatedCredentials.getUsername(), updatedCredentials.getPassword());
        return "redirect:/admin/account";
    }
 
    
    //ottiene l'utente registrato 
    
    @PostMapping("/deleteAccount/{id}")
    public String deleteAccount(@PathVariable("id") Long id, Model model) {
        Credentials credentials = credentialsService.getCredentials(id);
        if (credentials != null) {
            credentialsService.deleteCredentials(id);
            return "redirect:/logout";  // dopo eliminazione logout o redirect appropriato
        }
        model.addAttribute("error", "Utente non trovato");
        return "error";
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        Credentials credentials = credentialsService.getCredentials(id);
        if (credentials == null || !credentials.getRole().equals(Credentials.DEFAULT_ROLE)) {
            model.addAttribute("error", "Utente non trovato o non è un utente normale");
            return "redirect:/admin/account";
        }
        credentialsService.deleteCredentials(id);
        
        return "redirect:/admin/account";  // dopo eliminazione torna alla pagina degli account
    }
    

}
