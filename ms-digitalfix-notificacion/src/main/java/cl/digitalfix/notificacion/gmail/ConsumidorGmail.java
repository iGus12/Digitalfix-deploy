package cl.digitalfix.notificacion.gmail;

import cl.digitalfix.notificacion.excepcion.ErrorNoRecuperableException;
import cl.digitalfix.notificacion.excepcion.ErrorRecuperableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConsumidorGmail {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorGmail.class);

    private final NotificacionServiceGmail servicioGmail;
    private final ObjectMapper objectMapper;

    public ConsumidorGmail(NotificacionServiceGmail servicioGmail, ObjectMapper objectMapper) {
        this.servicioGmail = servicioGmail;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${digitalfix.mensajes.colas.gmail}")
    public void procesarMensaje(Message mensaje, Channel canal) throws IOException {
        long tagEntrega = mensaje.getMessageProperties().getDeliveryTag();
        boolean fueReentregado = Boolean.TRUE.equals(mensaje.getMessageProperties().getRedelivered());

        try {
            String cuerpoTexto = new String(mensaje.getBody());
            GmailMensaje datos = objectMapper.readValue(cuerpoTexto, GmailMensaje.class);

            servicioGmail.enviarCorreo(datos);
            canal.basicAck(tagEntrega, false);
            log.info("Mensaje de correo procesado correctamente");

        } catch (ErrorNoRecuperableException e) {
            log.error("Error no recuperable al procesar correo. Enviando a DLQ: {}", e.getMessage());
            canal.basicNack(tagEntrega, false, false);

        } catch (ErrorRecuperableException e) {
            if (!fueReentregado) {
                log.warn("Error recuperable. Se reintenta el mensaje: {}", e.getMessage());
                canal.basicNack(tagEntrega, false, true);
            } else {
                log.error("Error recuperable repetido. Enviando a DLQ: {}", e.getMessage());
                canal.basicNack(tagEntrega, false, false);
            }

        } catch (Exception e) {
            log.error("JSON invalido o error inesperado. Enviando a DLQ", e);
            canal.basicNack(tagEntrega, false, false);
        }
    }
}
