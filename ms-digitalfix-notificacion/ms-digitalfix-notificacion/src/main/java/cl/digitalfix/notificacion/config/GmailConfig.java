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
public class GmailConfig {

    private final MensajesProperties propiedades;

    public GmailConfig(MensajesProperties propiedades) {
        this.propiedades = propiedades;
    }

    @Bean
    public Queue colaGmail() {
        return QueueBuilder.durable(propiedades.getColas().getGmail())
                .deadLetterExchange(propiedades.getExchanges().getDeadLetter())
                .deadLetterRoutingKey(propiedades.getRoutingKeys().getGmailDlq())
                .build();
    }

    @Bean
    public Queue colaGmailDlq() {
        return QueueBuilder.durable(propiedades.getColas().getGmailDlq()).build();
    }

    @Bean
    public Binding enlaceGmailDirecto(Queue colaGmail, DirectExchange exchangeDirecto) {
        return BindingBuilder.bind(colaGmail)
                .to(exchangeDirecto)
                .with(propiedades.getRoutingKeys().getGmailDirecta());
    }

    @Bean
    public Binding enlaceGmailTema(Queue colaGmail, TopicExchange exchangeTema) {
        return BindingBuilder.bind(colaGmail)
                .to(exchangeTema)
                .with(propiedades.getRoutingKeys().getGmailTema());
    }

    @Bean
    public Binding enlaceGmailDlq(Queue colaGmailDlq, DirectExchange exchangeDeadLetter) {
        return BindingBuilder.bind(colaGmailDlq)
                .to(exchangeDeadLetter)
                .with(propiedades.getRoutingKeys().getGmailDlq());
    }
}
