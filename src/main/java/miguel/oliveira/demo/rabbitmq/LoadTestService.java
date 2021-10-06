package miguel.oliveira.demo.rabbitmq;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.camel.ProducerTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoadTestService {

  private final ProducerTemplate producer;

  @Async
  public void produceRandomMessage() {
    producer.asyncRequestBody("direct:produce", buildRandomMessage());
  }

  private Message buildRandomMessage() {
    final Message message = buildBasicMessage();
    final Map<String, Object> nestedObject = new HashMap<>();
    nestedObject.put("obj1", buildBasicMessage());
    nestedObject.put("obj2", UUID.randomUUID().toString());
    nestedObject.put("obj3",
        new String[]{UUID.randomUUID().toString(), UUID.randomUUID().toString()});
    message.setNestedObject(nestedObject);
    return message;
  }

  private Message buildBasicMessage() {
    final Message message = new Message();
    message.setField1(UUID.randomUUID().toString());
    message.setField2(UUID.randomUUID().toString());
    message.setField2(UUID.randomUUID().toString());
    message.setArray(new String[]{UUID.randomUUID().toString(), UUID.randomUUID().toString()});
    return message;
  }

}
