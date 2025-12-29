package it.uniroma3.siw.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /* =======================
       ACCOUNT ADMIN
       ======================= */

    @GetMapping("/account")
    public String getAccount(Model model) {
        Credentials credentials = credentialsService.getCurrentCredentials();

        if (credentials == null) {
            return "admin/indexAdmin";
        }

        model.addAttribute("credentials", credentials);

        if (Credentials.ADMIN_ROLE.equals(credentials.getRole())) {
            List<Credentials> utenti = (List<Credentials>) credentialsService.findAll();
            // rimuove l'admin corrente dalla lista
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
    public String updateAccount(@ModelAttribute("credentials") Credentials updatedCredentials,
                                Model model) {

        Credentials current = credentialsService.getCurrentCredentials();

        if (current == null || !updatedCredentials.getId().equals(current.getId())) {
            model.addAttribute("error", "Accesso non autorizzato");
            return "redirect:/admin/account";
        }

        userService.updateUser(updatedCredentials.getUser());
        credentialsService.updateCredentials(updatedCredentials);

        // riautentica l'admin con le nuove credenziali
        credentialsService.autoLogin(
                updatedCredentials.getUsername(),
                updatedCredentials.getPassword()
        );

        return "redirect:/admin/account";
    }

    /* =======================
       DELETE ACCOUNT ADMIN
       ======================= */

    @PostMapping("/deleteAccount/{id}")
    public String deleteAccount(@PathVariable("id") Long id, Model model) {
        Credentials credentials = credentialsService.getCredentials(id);

        if (credentials != null) {
            credentialsService.deleteCredentials(id);
            return "redirect:/logout";
        }

        model.addAttribute("error", "Utente non trovato");
        return "error";
    }

    /* =======================
       DELETE USER (ADMIN)
       ======================= */

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        Credentials credentials = credentialsService.getCredentials(id);

        // controllo null-safe + solo USER eliminabili
        if (credentials == null ||
            !Credentials.DEFAULT_ROLE.equals(credentials.getRole())) {
            model.addAttribute("error", "Utente non trovato o non eliminabile");
            return "redirect:/admin/account";
        }

        credentialsService.deleteCredentials(id);
        return "redirect:/admin/account";
    }
}
