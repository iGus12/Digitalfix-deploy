
package cl.digitalfix.notificacion.interna;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacionInternaRepository
        extends JpaRepository<NotificacionInterna, Long> {

    List<NotificacionInterna> findByUsuarioIdOrderByFechaCreacionDesc(
            String usuarioId
    );

    long countByUsuarioIdAndLeidaFalse(String usuarioId);

    Optional<NotificacionInterna> findByIdAndUsuarioId(
            Long id,
            String usuarioId
    );
}
