
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

    @Bean
    public Queue colaNotificacionesInternas() {
        return QueueBuilder
                .durable(propiedades.getColas().getInterna())
                .build();
    }

    @Bean
    public Binding bindingNotificacionesInternas(
            @Qualifier("colaNotificacionesInternas") Queue cola,
            @Qualifier("exchangeDirecto") DirectExchange exchange) {

        return BindingBuilder
                .bind(cola)
                .to(exchange)
                .with(propiedades.getRoutingKeys().getInternaDirecta());
    }
}
