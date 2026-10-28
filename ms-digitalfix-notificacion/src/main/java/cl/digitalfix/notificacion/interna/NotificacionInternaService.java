
package cl.digitalfix.notificacion.interna;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificacionInternaService {

    private final AtomicLong secuencia = new AtomicLong(1);

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<NotificacionInterna>>
            notificacionesPorUsuario = new ConcurrentHashMap<>();

    public NotificacionInterna crear(NotificacionInterna notificacion) {

        if (notificacion.getUsuarioId() == null
                || notificacion.getUsuarioId().isBlank()) {
            throw new IllegalArgumentException("El usuarioId es obligatorio");
        }

        notificacion.setId(secuencia.getAndIncrement());
        notificacion.setLeida(false);

        notificacionesPorUsuario
                .computeIfAbsent(
                        notificacion.getUsuarioId(),
                        clave -> new CopyOnWriteArrayList<>()
                )
                .add(notificacion);

        return notificacion;
    }

    public List<NotificacionInterna> listarPorUsuario(String usuarioId) {
        return List.copyOf(
                notificacionesPorUsuario.getOrDefault(
                        usuarioId,
                        new CopyOnWriteArrayList<>()
                )
        );
    }

    public long contarNoLeidas(String usuarioId) {
        return listarPorUsuario(usuarioId).stream()
                .filter(notificacion -> !notificacion.isLeida())
                .count();
    }

    public boolean marcarComoLeida(String usuarioId, Long notificacionId) {

        for (NotificacionInterna notificacion : listarPorUsuario(usuarioId)) {

            if (notificacion.getId().equals(notificacionId)) {
                notificacion.setLeida(true);
                return true;
            }
        }

        return false;
    }
}
