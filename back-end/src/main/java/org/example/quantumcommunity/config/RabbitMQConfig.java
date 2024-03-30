package org.example.quantumcommunity.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaol
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue commentQueue1() {
        return new Queue("commentQueue1");
    }

    @Bean
    public Queue commentQueue2() {
        return new Queue("commentQueue2");
    }

    @Bean
    public Queue commentQueue3() {
        return new Queue("commentQueue3");
    }
}
