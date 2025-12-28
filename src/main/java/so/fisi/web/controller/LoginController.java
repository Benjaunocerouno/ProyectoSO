package so.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import so.fisi.web.model.Usuario;
import so.fisi.web.model.dao.IUsuarioDAO;

@Controller
public class LoginController {

    @Autowired
    private IUsuarioDAO usuarioDAO;

    // Pantalla de Login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Procesar Login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email, @RequestParam String password, 
                               HttpSession session, RedirectAttributes flash) {
        
        return usuarioDAO.findByEmail(email)
                .filter(u -> u.getPassword().equals(password))
                .map(u -> {
                    session.setAttribute("usuarioLogueado", u);
                    return "redirect:/";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("error", "Credenciales incorrectas");
                    return "redirect:/login";
                });
    }

    // Pantalla de Registro
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Procesar Registro
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario, RedirectAttributes flash) {
        try {
            usuario.setEstado(true);
            usuarioDAO.save(usuario);
            flash.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "El email ya está registrado.");
            return "redirect:/registro";
        }
    }

    // Cerrar Sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}