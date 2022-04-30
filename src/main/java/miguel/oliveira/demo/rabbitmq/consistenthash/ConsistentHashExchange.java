package miguel.oliveira.demo.rabbitmq.consistenthash;

import java.util.Random;
import lombok.AllArgsConstructor;
import miguel.oliveira.demo.rabbitmq.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsistentHashExchange extends RouteBuilder {

  final static String EXCHANGE_NAME = "consistent-hash-exchange";
  final static String HASH_HEADER = "hash-on";

  private final ConsistentHashConsumer consistentHashConsumer;
  private final ConsistentHashProducer consistentHashProducer;

  @Override
  public void configure() {
    declareConsumers();
    declareProducerStarter();
  }

  private void declareConsumers() {
    final Random random = new Random();
    for (int i = 1; i <= 10; i++) {
      final String route = String.format(
          "rabbitmq:%s?exchangeType=x-consistent-hash&routingKey=%s&prefetchEnabled=true&prefetchCount=2&arg.exchange.hash-header=%s",
          EXCHANGE_NAME,
          random.nextInt(1, 10000),
          HASH_HEADER
      );

      from(route)
          .unmarshal().json(JsonLibrary.Jackson, Message.class)
          .bean(consistentHashConsumer);
    }
  }

  private void declareProducerStarter() {
    from("rabbitmq:produce-to-consistent-hash?exchangeType=direct")
        .bean(consistentHashProducer, "produce");
  }
}
