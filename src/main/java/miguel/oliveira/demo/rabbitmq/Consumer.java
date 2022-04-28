package miguel.oliveira.demo.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Consumer {

  public void consume(Payload payload) {
    log.info("Got a new message -> {}", payload.toString());
  }

  public void consume(Message message) {
    log.info("Got a new message -> {}", message.toString());
  }
}
