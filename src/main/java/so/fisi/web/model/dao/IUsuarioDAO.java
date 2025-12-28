package so.fisi.web.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import so.fisi.web.model.Usuario;

@Repository
public interface IUsuarioDAO extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    @Query("SELECT u FROM Usuario u JOIN u.libros l WHERE u.estado = true GROUP BY u.id, u.nombre, u.email, u.password, u.rol, u.estado, u.fechaCreacion, u.fechaModificacion ORDER BY COUNT(l) DESC")
    List<Usuario> findTopAutoresByPublicaciones(Pageable pageable);

    @Query("SELECT u FROM Usuario u JOIN u.libros l WHERE u.estado = true GROUP BY u.id, u.nombre, u.email, u.password, u.rol, u.estado, u.fechaCreacion, u.fechaModificacion ORDER BY SUM(l.vistas) DESC")
    List<Usuario> findTopAutoresByVistas(Pageable pageable);
}