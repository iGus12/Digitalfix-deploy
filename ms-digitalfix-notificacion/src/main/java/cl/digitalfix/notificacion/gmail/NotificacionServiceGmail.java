package cl.digitalfix.notificacion.gmail;

import cl.digitalfix.notificacion.excepcion.ErrorNoRecuperableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServiceGmail {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceGmail.class);

    public void enviarCorreo(GmailMensaje datos) {
        if (datos == null) {
            throw new ErrorNoRecuperableException("El mensaje de correo no puede ser nulo");
        }

        if (datos.para() == null || datos.para().isBlank()) {
            throw new ErrorNoRecuperableException("El campo 'para' es obligatorio");
        }

        if (datos.mensaje() == null || datos.mensaje().isBlank()) {
            throw new ErrorNoRecuperableException("El campo 'mensaje' es obligatorio");
        }

        log.info("Simulando envio de correo a: {} con asunto: {} para orden: {}",
                datos.para(), datos.asunto(), datos.ordenId());
    }
}
