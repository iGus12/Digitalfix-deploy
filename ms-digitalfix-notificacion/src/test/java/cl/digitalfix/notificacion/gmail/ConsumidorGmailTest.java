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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsumidorGmailTest {

    @Mock
    private NotificacionServiceGmail servicioGmail;

    @Mock
    private Channel canal;

    @Mock
    private Message mensaje;

    @Mock
    private MessageProperties propiedadesMensaje;

    private ConsumidorGmail consumidorGmail;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        consumidorGmail = new ConsumidorGmail(servicioGmail, objectMapper);
        when(mensaje.getMessageProperties()).thenReturn(propiedadesMensaje);
        when(propiedadesMensaje.getDeliveryTag()).thenReturn(1L);
    }

    @Test
    void testAckCorrecto() throws Exception {
        String jsonValido = "{\"para\":\"cliente@test.com\",\"asunto\":\"Aviso\",\"mensaje\":\"Hola\",\"ordenId\":\"ORD-123\"}";
        when(mensaje.getBody()).thenReturn(jsonValido.getBytes());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(canal).basicAck(1L, false);
    }

    @Test
    void testJsonInvalidoADlq() throws Exception {
        String jsonMalo = "esto-no-es-un-json";
        when(mensaje.getBody()).thenReturn(jsonMalo.getBytes());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(canal).basicNack(1L, false, false);
    }

    @Test
    void testErrorNoRecuperableADlq() throws Exception {
        String jsonSinCampos = "{\"asunto\":\"Sin correo\"}";
        when(mensaje.getBody()).thenReturn(jsonSinCampos.getBytes());
        doThrow(new ErrorNoRecuperableException("Falta campo obligatorio"))
                .when(servicioGmail).enviarCorreo(any());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(canal).basicNack(1L, false, false);
    }

    @Test
    void testErrorRecuperableConUnReintento() throws Exception {
        String jsonValido = "{\"para\":\"cliente@test.com\",\"mensaje\":\"Hola\"}";
        when(mensaje.getBody()).thenReturn(jsonValido.getBytes());
        when(propiedadesMensaje.getRedelivered()).thenReturn(false);
        doThrow(new ErrorRecuperableException("Fallo temporal de conexion"))
                .when(servicioGmail).enviarCorreo(any());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(canal).basicNack(1L, false, true);
    }

    @Test
    void testErrorRecuperableRepetidoADlq() throws Exception {
        String jsonValido = "{\"para\":\"cliente@test.com\",\"mensaje\":\"Hola\"}";
        when(mensaje.getBody()).thenReturn(jsonValido.getBytes());
        when(propiedadesMensaje.getRedelivered()).thenReturn(true);
        doThrow(new ErrorRecuperableException("Fallo temporal recurrente"))
                .when(servicioGmail).enviarCorreo(any());

        consumidorGmail.procesarMensaje(mensaje, canal);

        verify(canal).basicNack(1L, false, false);
    }
}
