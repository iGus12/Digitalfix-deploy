
package cl.digitalfix.notificacion.interna;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICACIONES_INTERNAS")
public class NotificacionInterna {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "notificacion_seq"
    )
    @SequenceGenerator(
        name = "notificacion_seq",
        sequenceName = "NOTIFICACION_SEQ",
        allocationSize = 1
    )
    private Long id;

    @Column(name = "USUARIO_ID", nullable = false, length = 255)
    private String usuarioId;

    @Column(name = "TITULO", nullable = false, length = 255)
    private String titulo;

    @Column(name = "MENSAJE", nullable = false, length = 2000)
    private String mensaje;

    @Column(name = "ORDEN_ID")
    private Long ordenId;

    @Column(name = "LEIDA", nullable = false)
    private boolean leida = false;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public NotificacionInterna() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getOrdenId() {
        return ordenId;
    }

    public void setOrdenId(Long ordenId) {
        this.ordenId = ordenId;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
