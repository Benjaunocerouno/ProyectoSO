package so.fisi.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import so.fisi.web.model.Libro;
import so.fisi.web.model.Usuario;
import so.fisi.web.model.dao.ILibroDAO;

@Controller
public class ReportesController {

    @Autowired
    private ILibroDAO libroDAO;

    @GetMapping("/reportes")
    public String verReportes(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        // VALIDACIÓN DE SEGURIDAD: 
        // Si no está logueado O el rol no es AUTOR, redirigir al inicio
        if (usuario == null || usuario.getRol() != so.fisi.web.model.Rol.AUTOR) {
            return "redirect:/";
        }

        List<Libro> misLibros = libroDAO.findByAutorUsuarioId(usuario.getId());

        // Cálculos dinámicos (se mantienen igual)
        int totalVistas = misLibros.stream().mapToInt(l -> l.getVistas() != null ? l.getVistas() : 0).sum();
        int totalLibros = misLibros.size();
        double espacioTotal = misLibros.stream().mapToLong(l -> l.getTamanoBytes() != null ? l.getTamanoBytes() : 0L).sum() / (1024.0 * 1024.0);

        model.addAttribute("libros", misLibros);
        model.addAttribute("totalLibros", totalLibros);
        model.addAttribute("totalVistas", totalVistas);
        model.addAttribute("espacioTotal", espacioTotal);
        
        return "reportes";
    }
}