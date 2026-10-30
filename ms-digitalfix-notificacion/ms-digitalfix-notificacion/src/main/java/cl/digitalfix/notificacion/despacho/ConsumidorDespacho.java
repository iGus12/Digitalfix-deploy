package cl.digitalfix.notificacion.despacho;

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
public class ConsumidorDespacho {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorDespacho.class);

    private final NotificacionServiceDespacho servicioDespacho;
    private final ObjectMapper objectMapper;

    public ConsumidorDespacho(NotificacionServiceDespacho servicioDespacho, ObjectMapper objectMapper) {
        this.servicioDespacho = servicioDespacho;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${digitalfix.mensajes.colas.despacho}")
    public void procesarMensaje(Message mensaje, Channel canal) throws IOException {
        long tagEntrega = mensaje.getMessageProperties().getDeliveryTag();
        boolean fueReentregado = Boolean.TRUE.equals(mensaje.getMessageProperties().getRedelivered());

        try {
            String cuerpoTexto = new String(mensaje.getBody());
            DespachoMensaje datos = objectMapper.readValue(cuerpoTexto, DespachoMensaje.class);

            servicioDespacho.notificarDespacho(datos);
            canal.basicAck(tagEntrega, false);
            log.info("Mensaje de despacho procesado correctamente");

        } catch (ErrorNoRecuperableException e) {
            log.error("Error no recuperable al procesar despacho. Enviando a DLQ: {}", e.getMessage());
            canal.basicNack(tagEntrega, false, false);

        } catch (ErrorRecuperableException e) {
            if (!fueReentregado) {
                log.warn("Error recuperable. Se reintenta el mensaje de despacho: {}", e.getMessage());
                canal.basicNack(tagEntrega, false, true);
            } else {
                log.error("Error recuperable repetido. Enviando a DLQ: {}", e.getMessage());
                canal.basicNack(tagEntrega, false, false);
            }

        } catch (Exception e) {
            log.error("JSON invalido o error inesperado en despacho. Enviando a DLQ", e);
            canal.basicNack(tagEntrega, false, false);
        }
    }
}
