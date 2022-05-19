package miguel.oliveira.demo.rabbitmq.consistenthash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import miguel.oliveira.demo.rabbitmq.Message;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsistentHashProducer {

  private final ProducerTemplate producer;
  private final ObjectMapper objectMapper;
  private final String consistentHashExchangeName;
  private final String hashHeader;

  public ConsistentHashProducer(
      ProducerTemplate producer,
      ObjectMapper objectMapper,
      @Value("${application.messaging.consistent-hash-exchange.name}") String consistentHashExchangeName,
      @Value("${application.messaging.consistent-hash-exchange.hash-header}") String hashHeader
  ) {
    this.producer = producer;
    this.objectMapper = objectMapper;
    this.consistentHashExchangeName = consistentHashExchangeName;
    this.hashHeader = hashHeader;
  }

  public void produce() throws JsonProcessingException {
    final Random random = new Random();
    for (int i = 0; i < 100; i++) {

      final String str = Character.toString((char) (random.nextInt(26) + 'a'));

      producer.sendBodyAndHeader(
          String.format("rabbitmq:%s?declare=false", consistentHashExchangeName),
          objectMapper.writeValueAsString(new Message(str, i)),
          hashHeader, str
      );
    }
  }


}
