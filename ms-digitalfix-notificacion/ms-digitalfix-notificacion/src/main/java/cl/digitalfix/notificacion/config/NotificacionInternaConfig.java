
package cl.digitalfix.notificacion.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class NotificacionInternaConfig {

    private final MensajesProperties propiedades;

    public NotificacionInternaConfig(MensajesProperties propiedades) {
        this.propiedades = propiedades;
    }

    // Cola principal de notificaciones internas
    // Su DLQ se configura mediante una politica de RabbitMQ.
    @Bean
    public Queue colaNotificacionesInternas() {
        return QueueBuilder
                .durable(propiedades.getColas().getInterna())
                .build();
    }

    // Enlace de la cola principal al exchange directo
    @Bean
    public Binding bindingNotificacionesInternas(
            @Qualifier("colaNotificacionesInternas") Queue cola,
            @Qualifier("exchangeDirecto") DirectExchange exchange) {

        return BindingBuilder
                .bind(cola)
                .to(exchange)
                .with(propiedades.getRoutingKeys().getInternaDirecta());
    }

    // Cola de mensajes fallidos de notificaciones internas
    @Bean
    public Queue colaNotificacionesInternasDlq() {
        return QueueBuilder
                .durable(propiedades.getColas().getInternaDlq())
                .build();
    }

    // Enlace entre la DLQ y el exchange de errores
    @Bean
    public Binding bindingNotificacionesInternasDlq(
            @Qualifier("colaNotificacionesInternasDlq") Queue cola,
            @Qualifier("exchangeDeadLetter") DirectExchange exchange) {

        return BindingBuilder
                .bind(cola)
                .to(exchange)
                .with(propiedades.getRoutingKeys().getInternaDlq());
    }
}
