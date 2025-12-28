package so.fisi.web.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "libro_v2")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SQLDelete(sql = "UPDATE libro_v2 SET estado = 0 WHERE id = ?") // Cambia 'false' por '0'
@Where(clause = "estado = 1") // Cambia 'true' por '1'
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String sinopsis; // El campo que te daba error
    
    private String categoria; // El campo que te daba error
    private String isbn;      // El campo que te daba error
    
    private String portadaUrl;
    private String archivoUrl;
    private Integer vistas = 0;
    private Long tamanoBytes = 0L; // Para el reporte de ocupación Azure
    
    private boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario autorUsuario;

    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Libro [id=" + id + ", titulo=" + titulo + ", sinopsis=" + sinopsis + ", categoria=" + categoria
                + ", isbn=" + isbn + ", portadaUrl=" + portadaUrl + ", archivoUrl=" + archivoUrl + ", vistas=" + vistas
                + ", tamanoBytes=" + tamanoBytes + ", estado=" + estado + ", autorUsuario=" + autorUsuario
                + ", fechaRegistro=" + fechaRegistro + "]";
    }
}