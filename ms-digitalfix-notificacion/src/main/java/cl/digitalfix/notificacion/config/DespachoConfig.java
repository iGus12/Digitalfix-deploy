package cl.digitalfix.notificacion.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DespachoConfig {

    private final MensajesProperties propiedades;

    public DespachoConfig(MensajesProperties propiedades) {
        this.propiedades = propiedades;
    }

    @Bean
    public Queue colaDespacho() {
        return QueueBuilder.durable(propiedades.getColas().getDespacho())
                .deadLetterExchange(propiedades.getExchanges().getDeadLetter())
                .deadLetterRoutingKey(propiedades.getRoutingKeys().getDespachoDlq())
                .build();
    }

    @Bean
    public Queue colaDespachoDlq() {
        return QueueBuilder.durable(propiedades.getColas().getDespachoDlq()).build();
    }

    @Bean
    public Binding enlaceDespachoDirecto(Queue colaDespacho, DirectExchange exchangeDirecto) {
        return BindingBuilder.bind(colaDespacho)
                .to(exchangeDirecto)
                .with(propiedades.getRoutingKeys().getDespachoDirecta());
    }

    @Bean
    public Binding enlaceDespachoTema(Queue colaDespacho, TopicExchange exchangeTema) {
        return BindingBuilder.bind(colaDespacho)
                .to(exchangeTema)
                .with(propiedades.getRoutingKeys().getDespachoTema());
    }

    @Bean
    public Binding enlaceDespachoDlq(Queue colaDespachoDlq, DirectExchange exchangeDeadLetter) {
        return BindingBuilder.bind(colaDespachoDlq)
                .to(exchangeDeadLetter)
                .with(propiedades.getRoutingKeys().getDespachoDlq());
    }
}
