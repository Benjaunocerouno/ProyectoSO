package so.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import so.fisi.web.model.Libro;
import so.fisi.web.model.dao.ILibroDAO;

@Controller
@RequestMapping("/libros")
public class LibroDetalleController {

    @Autowired
    private ILibroDAO libroDAO;

    // Si quieres usar la ruta de tu index actual, cambia "/ver/{id}" por "/detalle/{id}"
    @GetMapping("/ver/{id}") 
    public String verDetalle(@PathVariable Long id, Model model) {
        Libro libro = libroDAO.findById(id).orElse(null);
        
        if (libro == null) {
            return "redirect:/"; // Si no existe, al inicio
        }

        // Lógica de incremento de vistas
        libro.setVistas(libro.getVistas() + 1);
        libroDAO.save(libro);

        model.addAttribute("libro", libro);
        return "detalle"; 
    }
}