
package cl.digitalfix.notificacion.gmail;

import cl.digitalfix.notificacion.excepcion.ErrorNoRecuperableException;
import cl.digitalfix.notificacion.excepcion.ErrorRecuperableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServiceGmail {

    private static final Logger log =
            LoggerFactory.getLogger(NotificacionServiceGmail.class);

    private final JavaMailSender mailSender;
    private final String remitente;

    public NotificacionServiceGmail(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String remitente) {

        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    public void enviarCorreo(GmailMensaje datos) {

        if (datos == null) {
            throw new ErrorNoRecuperableException(
                    "El mensaje de correo no puede ser nulo"
            );
        }

        if (datos.para() == null || datos.para().isBlank()) {
            throw new ErrorNoRecuperableException(
                    "El campo 'para' es obligatorio"
            );
        }

        if (datos.mensaje() == null || datos.mensaje().isBlank()) {
            throw new ErrorNoRecuperableException(
                    "El campo 'mensaje' es obligatorio"
            );
        }

        try {
            SimpleMailMessage correo = new SimpleMailMessage();

            correo.setFrom(remitente);
            correo.setTo(datos.para());
            correo.setSubject(
                    datos.asunto() != null
                            ? datos.asunto()
                            : "Notificacion DigitalFix"
            );
            correo.setText(datos.mensaje());

            mailSender.send(correo);

            log.info(
                    "Correo enviado correctamente para orden: {}",
                    datos.ordenId()
            );

        } catch (MailException error) {

            log.error(
                    "Error al enviar correo mediante Gmail SMTP",
                    error
            );

            throw new ErrorRecuperableException(
                    "No se pudo enviar el correo: "
                    + error.getMessage()
            );
        }
    }
}
