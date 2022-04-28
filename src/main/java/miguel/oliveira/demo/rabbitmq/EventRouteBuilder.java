package miguel.oliveira.demo.rabbitmq;

import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventRouteBuilder extends RouteBuilder {

  private final Consumer consumer;

  @Override
  public void configure() {
    from("rabbitmq:amq.topic?skipExchangeDeclare=true&routingKey=test")
        .unmarshal().json(JsonLibrary.Jackson, Message.class)
        .bean(consumer);
  }
}
