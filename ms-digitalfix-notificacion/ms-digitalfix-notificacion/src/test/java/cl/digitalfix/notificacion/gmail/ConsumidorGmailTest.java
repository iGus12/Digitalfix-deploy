
package cl.digitalfix.notificacion.gmail;

import cl.digitalfix.notificacion.excepcion.ErrorNoRecuperableException;
import cl.digitalfix.notificacion.excepcion.ErrorRecuperableException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumidorGmailTest {

    @Mock
    private NotificacionServiceGmail servicioGmail;

    @Mock
    private Channel canal;

    private ConsumidorGmail consumidorGmail;

    private static final long DELIVERY_TAG = 1L;

    @BeforeEach
    void setup() {
        consumidorGmail = new ConsumidorGmail(
                servicioGmail,
                new ObjectMapper()
        );
    }

    private Message crearMensaje(String json) {
        MessageProperties propiedades = new MessageProperties();
        propiedades.setDeliveryTag(DELIVERY_TAG);

        return new Message(
                json.getBytes(StandardCharsets.UTF_8),
                propiedades
        );
    }

    @Test
    void testAckCorrecto() throws Exception {
        Message mensaje = crearMensaje(
            "{\"para\":\"cliente@test.com\","
            + "\"asunto\":\"Aviso\","
            + "\"mensaje\":\"Hola\","
            + "\"ordenId\":\"ORD-123\"}"
        );

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(servicioGmail, times(1)).enviarCorreo(any());
        verify(canal).basicAck(DELIVERY_TAG, false);
        verify(canal, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void testJsonInvalidoADlq() throws Exception {
        Message mensaje = crearMensaje("esto-no-es-un-json");

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(servicioGmail, never()).enviarCorreo(any());
        verify(canal).basicNack(DELIVERY_TAG, false, false);
    }

    @Test
    void testErrorNoRecuperableADlq() throws Exception {
        Message mensaje = crearMensaje(
            "{\"para\":\"\","
            + "\"asunto\":\"Sin correo\","
            + "\"mensaje\":\"Hola\"}"
        );

        doThrow(
            new ErrorNoRecuperableException("Falta campo obligatorio")
        ).when(servicioGmail).enviarCorreo(any());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(servicioGmail, times(1)).enviarCorreo(any());
        verify(canal).basicNack(DELIVERY_TAG, false, false);
        verify(canal, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void testErrorRecuperableConExitoPosterior() throws Exception {
        Message mensaje = crearMensaje(
            "{\"para\":\"cliente@test.com\","
            + "\"mensaje\":\"Hola\"}"
        );

        doThrow(
            new ErrorRecuperableException("Fallo temporal de conexion")
        ).doNothing()
         .when(servicioGmail).enviarCorreo(any());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(servicioGmail, times(2)).enviarCorreo(any());
        verify(canal).basicAck(DELIVERY_TAG, false);
        verify(canal, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void testErrorRecuperableTresIntentosADlq() throws Exception {
        Message mensaje = crearMensaje(
            "{\"para\":\"cliente@test.com\","
            + "\"mensaje\":\"Hola\"}"
        );

        doThrow(
            new ErrorRecuperableException("Fallo temporal recurrente")
        ).when(servicioGmail).enviarCorreo(any());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(servicioGmail, times(3)).enviarCorreo(any());
        verify(canal).basicNack(DELIVERY_TAG, false, false);
        verify(canal, never()).basicAck(anyLong(), anyBoolean());
    }
}
