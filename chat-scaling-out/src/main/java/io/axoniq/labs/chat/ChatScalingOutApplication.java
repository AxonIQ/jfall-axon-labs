package io.axoniq.labs.chat;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.SQLException;

@SpringBootApplication
public class ChatScalingOutApplication {

	private static final Logger logger = LoggerFactory.getLogger(ChatScalingOutApplication.class);

	public static void main(String[] args) throws SQLException {
		SpringApplication.run(ChatScalingOutApplication.class, args);
	}

	@Configuration
    public static class AmqpConfiguration {

	    @Bean
        public Exchange eventsExchange() {
	        return ExchangeBuilder.fanoutExchange("events").build();
        }

        @Bean
        public Queue participantsEventsQueue() {
	        return QueueBuilder.durable("participant-events").build();
        }

        @Bean
        public Binding participantsEventsBinding() {
	        return BindingBuilder.bind(participantsEventsQueue()).to(eventsExchange()).with("*").noargs();
        }

        @Autowired
        public void configure(AmqpAdmin admin) {
	        admin.declareExchange(eventsExchange());
	        admin.declareQueue(participantsEventsQueue());
	        admin.declareBinding(participantsEventsBinding());
        }

        @Bean
        public SpringAMQPMessageSource participantsEvents(Serializer serializer) {
	        return new SpringAMQPMessageSource(serializer) {
                @RabbitListener(queues = "participant-events", exclusive = true)
	            @Override
                public void onMessage(Message message, Channel channel) throws Exception {
                    super.onMessage(message, channel);
                }
            };
        }
    }

	@Configuration
    @EnableSwagger2
    public static class SwaggerConfig {
        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
              .select()
              .apis(RequestHandlerSelectors.any())
              .paths(PathSelectors.any())
              .build();
        }
    }
}
