package miguel.oliveira.demo.record;

import lombok.AllArgsConstructor;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RecordingEventRouteBuilder extends RouteBuilder {

  private final PlaybackService playbackService;

  @Override
  public void configure() throws Exception {
    from("direct:record")
        .setHeader(RabbitMQConstants.ROUTING_KEY, constant("record"))
        .to(ExchangePattern.InOnly, "rabbitmq:record?declare=false");

    from("rabbitmq:record?skipExchangeDeclare=true&routingKey=record")
        .bean(playbackService);
  }

}
