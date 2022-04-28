package miguel.oliveira.demo.replay;

import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReplayEventRoute extends RouteBuilder {

  private final Replayer replayer;

  @Override
  public void configure() throws Exception {
    from("rabbitmq:amq.topic?skipExchangeDeclare=true&routingKey=replay")
        .bean(replayer);
  }

}
