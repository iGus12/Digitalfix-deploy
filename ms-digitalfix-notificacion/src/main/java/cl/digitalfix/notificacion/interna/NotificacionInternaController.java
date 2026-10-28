
package cl.digitalfix.notificacion.interna;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionInternaController {

    private final NotificacionInternaService servicio;

    public NotificacionInternaController(
            NotificacionInternaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionInterna>> listar(
            @AuthenticationPrincipal Jwt jwt) {

        String usuarioId = jwt.getSubject();

        return ResponseEntity.ok(
                servicio.listarPorUsuario(usuarioId)
        );
    }

    @GetMapping("/no-leidas")
    public ResponseEntity<Long> contarNoLeidas(
            @AuthenticationPrincipal Jwt jwt) {

        String usuarioId = jwt.getSubject();

        return ResponseEntity.ok(
                servicio.contarNoLeidas(usuarioId)
        );
    }

    @PatchMapping("/{id}/leida")
    public ResponseEntity<Void> marcarComoLeida(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        String usuarioId = jwt.getSubject();

        boolean encontrada =
                servicio.marcarComoLeida(usuarioId, id);

        if (!encontrada) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
