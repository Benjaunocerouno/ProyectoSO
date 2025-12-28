package so.fisi.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import so.fisi.web.model.Libro;
import so.fisi.web.model.Rol;
import so.fisi.web.model.Usuario;
import so.fisi.web.model.dao.ILibroDAO;
import so.fisi.web.model.dao.IUsuarioDAO;

@Service
public class BibliotecaService {

    @Autowired
    private ILibroDAO libroDAO;

    @Autowired
    private IUsuarioDAO usuarioDAO;

    // ==========================================
    // 1. MÉTODOS PARA EL INDEX (RANKINGS)
    // ==========================================

    public List<Libro> obtenerLibrosPopulares() {
        try {
            List<Libro> libros = libroDAO.findTop10ByOrderByVistasDesc();
            return (libros != null) ? libros : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Usuario> obtenerAutoresMasProductivos() {
        try {
            return usuarioDAO.findTopAutoresByPublicaciones(PageRequest.of(0, 5));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Usuario> obtenerAutoresMasVistos() {
        try {
            return usuarioDAO.findTopAutoresByVistas(PageRequest.of(0, 5));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // ==========================================
    // 2. GESTIÓN DE PERFIL Y SEGURIDAD
    // ==========================================

    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioDAO.findById(id).orElse(null);
    }

    /**
     * Actualiza el nombre y, opcionalmente, la contraseña del usuario.
     * El email no se toca para mantener la integridad de la cuenta.
     */
    public void actualizarPerfil(Long usuarioId, String nuevoNombre, String actual, String nueva, String confirmar) throws Exception {
        Usuario usuario = usuarioDAO.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado."));

        // Validar y actualizar nombre
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new Exception("El nombre no puede estar vacío.");
        }
        usuario.setNombre(nuevoNombre);

        // Lógica de cambio de contraseña (solo si se llena el campo 'Contraseña Actual')
        if (actual != null && !actual.isEmpty()) {
            // Verificar que la actual sea correcta
            if (!usuario.getPassword().equals(actual)) {
                throw new Exception("La contraseña actual es incorrecta.");
            }
            // Verificar que la nueva coincida con la confirmación
            if (!nueva.equals(confirmar)) {
                throw new Exception("La nueva contraseña y su confirmación no coinciden.");
            }
            // Validar longitud
            if (nueva.length() < 6) {
                throw new Exception("La nueva contraseña debe tener al menos 6 caracteres.");
            }
            usuario.setPassword(nueva);
        }

        usuarioDAO.save(usuario);
    }

    // ==========================================
    // 3. GESTIÓN DE LIBROS (CRUD)
    // ==========================================

    /**
     * Registra un libro verificando el Rol del usuario.
     */
    public void registrarLibro(Libro libro, Usuario autor) throws Exception {
        if (autor.getRol() != Rol.AUTOR) {
            throw new Exception("Acceso denegado: Solo los autores pueden subir libros.");
        }
        
        libro.setAutorUsuario(autor);
        libro.setEstado(true);
        libro.setVistas(0);
        
        libroDAO.save(libro);
    }
}