package so.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import so.fisi.web.service.BibliotecaService;

@Controller
public class IndexController {

    @Autowired
    private BibliotecaService bibliotecaService;

    // Inyectamos el valor de la variable de entorno (Parte 3)
    // Si no existe, tomará "Desarrollo_Local" por defecto
    @Value("${app.env}")
    private String entorno;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        
        // 1. CARGA DE DATOS PARA RANKINGS (BibliotecaService)
        // Obtenemos los 10 libros con más vistas
        model.addAttribute("librosPopulares", bibliotecaService.obtenerLibrosPopulares());
        
        // Obtenemos los 5 autores con más libros publicados
        model.addAttribute("autoresProductivos", bibliotecaService.obtenerAutoresMasProductivos());
        
        // Obtenemos los 5 autores cuyas obras suman más vistas
        model.addAttribute("autoresMasVistos", bibliotecaService.obtenerAutoresMasVistos());

        // 2. SOPORTE DE ENTORNOS MÚLTIPLES (Azure Slots)
        // Pasamos el nombre del ambiente para el badge visual
        model.addAttribute("entorno", entorno.toUpperCase());
        
        // 3. DATOS DE SESIÓN
        // Verificamos si hay un usuario logueado para personalizar la bienvenida
        model.addAttribute("usuario", session.getAttribute("usuarioLogueado"));

        return "index"; // Carga src/main/resources/templates/index.html
    }
}