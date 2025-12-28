package so.fisi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import so.fisi.web.service.BibliotecaService;

@Controller
public class IndexController {

    @Autowired
    private BibliotecaService bibliotecaService;

    @GetMapping("/")
    public String index(Model model) {
        // Pasamos los datos a la vista (index.html)
        model.addAttribute("librosPopulares", bibliotecaService.obtenerLibrosPopulares());
        model.addAttribute("autoresProductivos", bibliotecaService.obtenerAutoresMasProductivos());
        model.addAttribute("autoresMasVistos", bibliotecaService.obtenerAutoresMasVistos());
        
        return "index"; // Esto busca el archivo src/main/resources/templates/index.html
    }
}