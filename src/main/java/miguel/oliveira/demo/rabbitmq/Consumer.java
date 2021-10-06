package miguel.oliveira.demo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

  public void consume(Payload payload) {
    LOGGER.info("Got a new message -> {}", payload.toString());
  }

  public void consume(Message message) {
    LOGGER.info("Got a new message -> {}", message.toString());
  }
}
