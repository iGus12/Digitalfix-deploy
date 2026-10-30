
package cl.digitalfix.notificacion.interna;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NotificacionInternaConsumer {

    private final NotificacionInternaService servicio;
    private final ObjectMapper objectMapper;

    public NotificacionInternaConsumer(
            NotificacionInternaService servicio,
            ObjectMapper objectMapper) {

        this.servicio = servicio;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(
        queues = "${digitalfix.mensajes.colas.interna}"
    )
    public void recibirMensaje(
            Message mensaje,
            Channel canal) throws IOException {

        long deliveryTag =
                mensaje.getMessageProperties().getDeliveryTag();

        try {
            String contenido = new String(
                    mensaje.getBody(),
                    java.nio.charset.StandardCharsets.UTF_8
            );

            NotificacionInterna notificacion =
                    objectMapper.readValue(
                            contenido,
                            NotificacionInterna.class
                    );

            if (notificacion.getTitulo() == null
                    || notificacion.getTitulo().isBlank()
                    || notificacion.getMensaje() == null
                    || notificacion.getMensaje().isBlank()) {

                throw new IllegalArgumentException(
                        "La notificacion requiere titulo y mensaje"
                );
            }

            servicio.crear(notificacion);

            canal.basicAck(deliveryTag, false);

            System.out.println(
                "[NOTIFICACIONES] Aviso interno registrado: "
                + notificacion.getTitulo()
            );

        } catch (Exception error) {

            System.err.println(
                "[NOTIFICACIONES] Error al procesar aviso: "
                + error.getMessage()
            );

            canal.basicNack(
                    deliveryTag,
                    false,
                    false
            );
        }
    }
}
