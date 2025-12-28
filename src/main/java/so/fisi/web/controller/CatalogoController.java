package so.fisi.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import so.fisi.web.model.Libro;
import so.fisi.web.model.Usuario;
import so.fisi.web.model.dao.ILibroDAO;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    @Autowired
    private ILibroDAO libroDAO;

    /**
     * Muestra libros disponibles. 
     * Si llega el parámetro 'misLibros=true', filtra por el usuario en sesión.
     */
    @GetMapping
    public String listarLibros(@RequestParam(name = "misLibros", required = false) Boolean misLibros, 
                               HttpSession session, 
                               Model model) {
        
        List<Libro> libros;
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        // Lógica de filtrado "De mi"
        if (Boolean.TRUE.equals(misLibros) && usuario != null) {
            libros = libroDAO.findByAutorUsuarioId(usuario.getId());
            model.addAttribute("tituloPagina", "Mis Publicaciones");
            model.addAttribute("filtroActivo", "misLibros");
        } else {
            // Obtenemos todos los libros activos (estado = 1)
            libros = libroDAO.findAll();
            model.addAttribute("tituloPagina", "Catálogo Completo");
        }
        
        model.addAttribute("libros", libros);
        return "catalogo"; 
    }

    @GetMapping("/buscar")
    public String buscarLibros(@RequestParam(name = "q", required = false) String query, Model model) {
        List<Libro> resultados;
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/catalogo";
        } else {
            resultados = libroDAO.findByTituloContainingIgnoreCase(query);
        }
        model.addAttribute("tituloPagina", "Resultados para: " + query);
        model.addAttribute("libros", resultados);
        return "catalogo";
    }
}