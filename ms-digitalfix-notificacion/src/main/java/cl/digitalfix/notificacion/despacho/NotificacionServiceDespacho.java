package cl.digitalfix.notificacion.despacho;

import cl.digitalfix.notificacion.excepcion.ErrorNoRecuperableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServiceDespacho {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceDespacho.class);

    public void notificarDespacho(DespachoMensaje datos) {
        if (datos == null) {
            throw new ErrorNoRecuperableException("El mensaje de despacho no puede ser nulo");
        }

        if (datos.tecnicoId() == null || datos.tecnicoId().isBlank()) {
            throw new ErrorNoRecuperableException("El campo 'tecnicoId' es obligatorio");
        }

        if (datos.ordenId() == null || datos.ordenId().isBlank()) {
            throw new ErrorNoRecuperableException("El campo 'ordenId' es obligatorio");
        }

        if (datos.direccion() == null || datos.direccion().isBlank()) {
            throw new ErrorNoRecuperableException("El campo 'direccion' es obligatorio");
        }

        log.info("Simulando asignacion de despacho - Tecnico: {}, Orden: {}, Direccion: {}",
                datos.tecnicoId(), datos.ordenId(), datos.direccion());
    }
}
