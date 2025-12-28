package so.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import so.fisi.web.model.Usuario;
import so.fisi.web.service.BibliotecaService;

@Controller
@RequestMapping("/ajustes")
public class AjustesController {

    @Autowired
    private BibliotecaService bibliotecaService;

    @GetMapping
    public String verAjustes(HttpSession session, Model model) {
        // Validamos que haya sesión iniciada
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "ajustes";
    }

    // Cambiamos el nombre del método y añadimos el parámetro 'nombre'
    @PostMapping("/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                @RequestParam(required = false) String actual, 
                                @RequestParam(required = false) String nueva, 
                                @RequestParam(required = false) String confirmar,
                                HttpSession session, 
                                RedirectAttributes flash) {
        
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuarioLogueado");
        
        // Si la sesión expiró justo antes de dar clic en guardar
        if (usuarioSesion == null) {
            flash.addFlashAttribute("error", "Tu sesión ha expirado. Por favor, inicia sesión de nuevo.");
            return "redirect:/login";
        }

        try {
            bibliotecaService.actualizarPerfil(usuarioSesion.getId(), nombre, actual, nueva, confirmar);
            
            // Actualizamos el objeto en sesión para que el nombre se vea reflejado en toda la web
            usuarioSesion.setNombre(nombre);
            session.setAttribute("usuarioLogueado", usuarioSesion);

            flash.addFlashAttribute("success", "¡Perfil actualizado correctamente!");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/ajustes";
    }
}