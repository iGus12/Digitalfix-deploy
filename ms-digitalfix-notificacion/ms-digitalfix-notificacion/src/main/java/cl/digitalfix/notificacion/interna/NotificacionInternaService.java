
package cl.digitalfix.notificacion.interna;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacionInternaService {

    private final NotificacionInternaRepository repository;

    public NotificacionInternaService(
            NotificacionInternaRepository repository) {
        this.repository = repository;
    }

    // Guardar una notificacion en Oracle
    @Transactional
    public NotificacionInterna crear(NotificacionInterna notificacion) {

        if (notificacion.getUsuarioId() == null
                || notificacion.getUsuarioId().isBlank()) {
            throw new IllegalArgumentException(
                    "El usuarioId es obligatorio"
            );
        }

        // El ID ahora lo genera Oracle
        notificacion.setId(null);
        notificacion.setLeida(false);

        return repository.save(notificacion);
    }

    // Listar notificaciones del usuario
    public List<NotificacionInterna> listarPorUsuario(String usuarioId) {
        return repository.findByUsuarioIdOrderByFechaCreacionDesc(
                usuarioId
        );
    }

    // Contar notificaciones sin leer
    public long contarNoLeidas(String usuarioId) {
        return repository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    // Marcar una notificacion como leida
    @Transactional
    public boolean marcarComoLeida(
            String usuarioId,
            Long notificacionId) {

        return repository.findByIdAndUsuarioId(
                notificacionId,
                usuarioId
        ).map(notificacion -> {
            notificacion.setLeida(true);
            repository.save(notificacion);
            return true;
        }).orElse(false);
    }
}
