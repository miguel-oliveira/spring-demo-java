package miguel.oliveira.demo.configuration;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

  @Bean
  public ConnectionFactory connectionFactory(AbstractConnectionFactory abstractConnectionFactory) {
    final ConnectionFactory connectionFactory = abstractConnectionFactory
        .getRabbitConnectionFactory();
    connectionFactory.setAutomaticRecoveryEnabled(true);
    return connectionFactory;
  }
}
