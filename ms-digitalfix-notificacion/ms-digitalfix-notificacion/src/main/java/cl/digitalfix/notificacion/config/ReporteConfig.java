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
public class ReporteConfig {

    private final MensajesProperties propiedades;

    public ReporteConfig(MensajesProperties propiedades) {
        this.propiedades = propiedades;
    }

    @Bean
    public Queue colaReporte() {
        return QueueBuilder.durable(propiedades.getColas().getReporte())
                .deadLetterExchange(propiedades.getExchanges().getDeadLetter())
                .deadLetterRoutingKey(propiedades.getRoutingKeys().getReporteDlq())
                .build();
    }

    @Bean
    public Queue colaReporteDlq() {
        return QueueBuilder.durable(propiedades.getColas().getReporteDlq()).build();
    }

    @Bean
    public Binding enlaceReporteDirecto(Queue colaReporte, DirectExchange exchangeDirecto) {
        return BindingBuilder.bind(colaReporte)
                .to(exchangeDirecto)
                .with(propiedades.getRoutingKeys().getReporteDirecta());
    }

    @Bean
    public Binding enlaceReporteTema(Queue colaReporte, TopicExchange exchangeTema) {
        return BindingBuilder.bind(colaReporte)
                .to(exchangeTema)
                .with(propiedades.getRoutingKeys().getReporteTema());
    }

    @Bean
    public Binding enlaceReporteDlq(Queue colaReporteDlq, DirectExchange exchangeDeadLetter) {
        return BindingBuilder.bind(colaReporteDlq)
                .to(exchangeDeadLetter)
                .with(propiedades.getRoutingKeys().getReporteDlq());
    }
}
