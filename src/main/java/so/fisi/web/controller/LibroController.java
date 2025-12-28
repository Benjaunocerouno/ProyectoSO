package so.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import so.fisi.web.model.Libro;
import so.fisi.web.model.Rol;
import so.fisi.web.model.Usuario;
import so.fisi.web.model.dao.ILibroDAO; // IMPORTANTE: Verifica que este import esté presente
import so.fisi.web.service.AzureStorageService;
import so.fisi.web.service.BibliotecaService;

@Controller
@RequestMapping("/libros")
public class LibroController {

    // Inyectamos el DAO para poder realizar búsquedas y eliminaciones directas
    @Autowired
    private ILibroDAO libroDAO;

    @Autowired
    private BibliotecaService bibliotecaService;

    @Autowired
    private AzureStorageService azureService;

    /**
     * Muestra el formulario para subir un nuevo libro.
     * Solo accesible para usuarios con rol AUTOR.
     */
    @GetMapping("/nuevo")
    public String formularioNuevoLibro(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null || usuario.getRol() != Rol.AUTOR) {
            return "redirect:/login";
        }

        model.addAttribute("libro", new Libro());
        return "form-libro";
    }

    /**
     * Procesa la subida de archivos a Azure y guarda el registro en SQL Server.
     */
    @PostMapping("/guardar")
    public String guardarLibro(@RequestParam("titulo") String titulo,
                            @RequestParam("sinopsis") String sinopsis,
                            @RequestParam("categoria") String categoria,
                            @RequestParam(value = "isbn", required = false) String isbn,
                            @RequestParam("filePortada") MultipartFile filePortada,
                            @RequestParam("filePdf") MultipartFile filePdf,
                            HttpSession session,
                            RedirectAttributes flash) {
        
        Usuario autor = (Usuario) session.getAttribute("usuarioLogueado");
        if (autor == null) return "redirect:/login";

        try {
            // 1. Validar que los archivos existan
            if (filePortada.isEmpty() || filePdf.isEmpty()) {
                flash.addFlashAttribute("error", "Debe subir tanto la portada como el PDF.");
                return "redirect:/libros/nuevo";
            }

            // 2. Subir a Azure
            String urlPortada = azureService.subirArchivo(filePortada);
            String urlPdf = azureService.subirArchivo(filePdf);

            // 3. Crear objeto Libro
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setSinopsis(sinopsis);
            libro.setCategoria(categoria);
            libro.setIsbn(isbn);
            libro.setPortadaUrl(urlPortada);
            libro.setArchivoUrl(urlPdf);
            libro.setTamanoBytes(filePdf.getSize());

            // 4. USAR EL SERVICIO para validar rol y guardar
            bibliotecaService.registrarLibro(libro, autor);
            
            flash.addFlashAttribute("success", "¡Libro '" + titulo + "' publicado con éxito!");
            return "redirect:/catalogo";

        } catch (Exception e) {
            e.printStackTrace();
            // El error se muestra ahora gracias al cambio en el HTML
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/libros/nuevo";
        }
    }
    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id, HttpSession session, RedirectAttributes flash) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return "redirect:/login";

        Libro libro = libroDAO.findById(id).orElse(null);

        // Verificamos que el libro sea del autor logueado
        if (libro != null && libro.getAutorUsuario().getId().equals(usuario.getId())) {
            // Al llamar a deleteById, se ejecuta el UPDATE estado=false por el @SQLDelete de la entidad
            libroDAO.deleteById(id); 
            flash.addFlashAttribute("success", "El libro ha sido retirado correctamente.");
        } else {
            flash.addFlashAttribute("error", "No tienes permisos para esta acción.");
        }
        return "redirect:/reportes";
    }

    @GetMapping("/editar/{id}")
    public String editarLibroForm(@PathVariable Long id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        Libro libro = libroDAO.findById(id).orElse(null);

        // Validar que el libro exista y que el autor sea quien lo subió
        if (libro != null && libro.getAutorUsuario().getId().equals(usuario.getId())) {
            model.addAttribute("libro", libro);
            return "form-editar"; 
        }
        return "redirect:/reportes";
    }

    @PostMapping("/actualizar-datos")
    public String actualizarDatosLibro(@RequestParam Long id, 
                                    @RequestParam String titulo, 
                                    @RequestParam String sinopsis, 
                                    HttpSession session, 
                                    RedirectAttributes flash) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        Libro libro = libroDAO.findById(id).orElse(null);

        if (libro != null && libro.getAutorUsuario().getId().equals(usuario.getId())) {
            libro.setTitulo(titulo);
            libro.setSinopsis(sinopsis);
            libroDAO.save(libro); // Actualiza en Azure SQL
            flash.addFlashAttribute("success", "¡Obra actualizada correctamente!");
        } else {
            flash.addFlashAttribute("error", "No tienes permisos para editar esta obra.");
        }
        return "redirect:/reportes";
    }
}