package miguel.oliveira.demo.rabbitmq.consistenthash;

import java.util.Random;
import miguel.oliveira.demo.rabbitmq.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConsistentHashExchange extends RouteBuilder {

  private final ConsistentHashConsumer consistentHashConsumer;
  private final ConsistentHashProducer consistentHashProducer;
  private final String consistentHashExchangeName;
  private final int concurrentConsumers;
  private final int prefetchCount;
  private final String hashHeader;
  private final String testTriggerExchangeName;

  public ConsistentHashExchange(
      ConsistentHashConsumer consistentHashConsumer,
      ConsistentHashProducer consistentHashProducer,
      @Value("${application.messaging.consistent-hash-exchange.name}") String consistentHashExchangeName,
      @Value("${application.messaging.consistent-hash-exchange.concurrent-consumers}") int concurrentConsumers,
      @Value("${application.messaging.consistent-hash-exchange.prefetch-count}") int prefetchCount,
      @Value("${application.messaging.consistent-hash-exchange.hash-header}") String hashHeader,
      @Value("${application.messaging.consistent-hash-exchange.trigger-test-exchange-name}") String testTriggerExchangeName) {
    this.consistentHashConsumer = consistentHashConsumer;
    this.consistentHashProducer = consistentHashProducer;
    this.consistentHashExchangeName = consistentHashExchangeName;
    this.concurrentConsumers = concurrentConsumers;
    this.prefetchCount = prefetchCount;
    this.hashHeader = hashHeader;
    this.testTriggerExchangeName = testTriggerExchangeName;
  }

  @Override
  public void configure() {
    declareConsumers();
    declareProducerStarter();
  }

  private void declareConsumers() {
    final Random random = new Random();
    for (int i = 0; i < concurrentConsumers; i++) {
      final String route = String.format(
          "rabbitmq:%s?exchangeType=x-consistent-hash&routingKey=%s&prefetchEnabled=true&prefetchCount=%s&arg.exchange.hash-header=%s",
          consistentHashExchangeName,
          random.nextInt(1, 10000),
          prefetchCount,
          hashHeader
      );

      from(route)
          .unmarshal().json(JsonLibrary.Jackson, Message.class)
          .bean(consistentHashConsumer);
    }
  }

  private void declareProducerStarter() {
    from(String.format("rabbitmq:%s?exchangeType=direct", testTriggerExchangeName))
        .bean(consistentHashProducer, "produce");
  }
}
