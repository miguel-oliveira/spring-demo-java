package miguel.oliveira.demo.rabbitmq;

import lombok.AllArgsConstructor;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventRouteBuilder extends RouteBuilder {

  private final Consumer consumer;

  @Override
  public void configure() throws Exception {
    from("direct:produce")
        .marshal()
        .json(JsonLibrary.Jackson)
        .setHeader(RabbitMQConstants.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
        .setHeader(RabbitMQConstants.ROUTING_KEY, constant("load"))
        .to(ExchangePattern.InOnly, "rabbitmq:topic?declare=false");

    from("rabbitmq:topic?skipExchangeDeclare=true&routingKey=load&exclusive=true")
        .unmarshal().json(JsonLibrary.Jackson, Message.class)
        .bean(consumer);
  }
}
