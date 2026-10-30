package cl.digitalfix.notificacion.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeConfig {

    private final MensajesProperties propiedades;

    public ExchangeConfig(MensajesProperties propiedades) {
        this.propiedades = propiedades;
    }

    @Bean
    public DirectExchange exchangeDirecto() {
        return new DirectExchange(propiedades.getExchanges().getDirecto());
    }

    @Bean
    public TopicExchange exchangeTema() {
        return new TopicExchange(propiedades.getExchanges().getTema());
    }

    @Bean
    public DirectExchange exchangeDeadLetter() {
        return new DirectExchange(propiedades.getExchanges().getDeadLetter());
    }
}
