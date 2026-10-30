
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
import java.nio.charset.StandardCharsets;

@Component
public class ConsumidorGmail {

    private static final Logger log =
            LoggerFactory.getLogger(ConsumidorGmail.class);

    private static final int MAX_INTENTOS = 3;
    private static final long ESPERA_MS = 2000;

    private final NotificacionServiceGmail servicioGmail;
    private final ObjectMapper objectMapper;

    public ConsumidorGmail(
            NotificacionServiceGmail servicioGmail,
            ObjectMapper objectMapper) {

        this.servicioGmail = servicioGmail;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${digitalfix.mensajes.colas.gmail}")
    public void procesarMensaje(
            Message mensaje,
            Channel canal) throws IOException {

        long tagEntrega =
                mensaje.getMessageProperties().getDeliveryTag();

        try {
            String cuerpoTexto = new String(
                    mensaje.getBody(),
                    StandardCharsets.UTF_8
            );

            GmailMensaje datos =
                    objectMapper.readValue(
                            cuerpoTexto,
                            GmailMensaje.class
                    );

            for (int intento = 1; intento <= MAX_INTENTOS; intento++) {

                try {
                    servicioGmail.enviarCorreo(datos);

                    canal.basicAck(tagEntrega, false);

                    log.info(
                            "Correo procesado correctamente en intento {} para orden {}",
                            intento,
                            datos.ordenId()
                    );

                    return;

                } catch (ErrorRecuperableException error) {

                    log.warn(
                            "Error temporal de Gmail. Intento {}/{}: {}",
                            intento,
                            MAX_INTENTOS,
                            error.getMessage()
                    );

                    if (intento == MAX_INTENTOS) {
                        log.error(
                                "Se agotaron los {} intentos. Enviando mensaje a DLQ.",
                                MAX_INTENTOS
                        );

                        canal.basicNack(tagEntrega, false, false);
                        return;
                    }

                    try {
                        Thread.sleep(ESPERA_MS);

                    } catch (InterruptedException interrupcion) {

                        Thread.currentThread().interrupt();

                        log.error(
                                "Reintento interrumpido. Enviando mensaje a DLQ."
                        );

                        canal.basicNack(tagEntrega, false, false);
                        return;
                    }
                }
            }

        } catch (ErrorNoRecuperableException error) {

            log.error(
                    "Error no recuperable. Enviando a DLQ: {}",
                    error.getMessage()
            );

            canal.basicNack(tagEntrega, false, false);

        } catch (Exception error) {

            log.error(
                    "JSON invalido o error inesperado. Enviando a DLQ",
                    error
            );

            canal.basicNack(tagEntrega, false, false);
        }
    }
}
