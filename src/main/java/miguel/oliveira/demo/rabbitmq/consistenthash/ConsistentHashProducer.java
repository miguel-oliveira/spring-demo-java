package miguel.oliveira.demo.rabbitmq.consistenthash;

import static miguel.oliveira.demo.rabbitmq.consistenthash.ConsistentHashExchange.EXCHANGE_NAME;
import static miguel.oliveira.demo.rabbitmq.consistenthash.ConsistentHashExchange.HASH_HEADER;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import miguel.oliveira.demo.rabbitmq.Message;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

@Component
public record ConsistentHashProducer(
    ProducerTemplate producer,
    ObjectMapper objectMapper
) {

  private final static String[] strings = new String[]{"a", "b", "c", "d", "e"};

  public void produce() throws JsonProcessingException {
    final Random random = new Random();
    for (int i = 0; i < 100; i++) {

      final String str = strings[random.nextInt(0, strings.length)];

      producer.sendBodyAndHeader(
          String.format("rabbitmq:%s?declare=false", EXCHANGE_NAME),
          objectMapper.writeValueAsString(new Message(str, i)),
          HASH_HEADER, str
      );
    }
  }


}
