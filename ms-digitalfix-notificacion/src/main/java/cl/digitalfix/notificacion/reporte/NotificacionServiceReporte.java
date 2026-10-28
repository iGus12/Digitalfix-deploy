package cl.digitalfix.notificacion.reporte;

import cl.digitalfix.notificacion.excepcion.ErrorNoRecuperableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServiceReporte {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceReporte.class);

    public void generarReporte(ReporteMensaje datos) {
        if (datos == null) {
            throw new ErrorNoRecuperableException("El mensaje de reporte no puede ser nulo");
        }

        if (datos.ordenId() == null || datos.ordenId().isBlank()) {
            throw new ErrorNoRecuperableException("El campo 'ordenId' es obligatorio");
        }

        if (datos.tipoReporte() == null || datos.tipoReporte().isBlank()) {
            throw new ErrorNoRecuperableException("El campo 'tipoReporte' es obligatorio");
        }

        log.info("Simulando generacion de reporte - Orden: {}, Tipo: {}, Correo Cliente: {}",
                datos.ordenId(), datos.tipoReporte(), datos.correoCliente());
    }
}
