package so.fisi.web.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import so.fisi.web.model.Libro;

@Repository
public interface ILibroDAO extends JpaRepository<Libro, Long> {
    
    // Spring Boot generará automáticamente el SQL para esto
    List<Libro> findTop10ByOrderByVistasDesc();
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findByAutorUsuarioId(Long usuarioId);
    
}