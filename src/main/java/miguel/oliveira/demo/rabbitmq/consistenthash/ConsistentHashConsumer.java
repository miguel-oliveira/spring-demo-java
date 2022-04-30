package miguel.oliveira.demo.rabbitmq.consistenthash;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import miguel.oliveira.demo.rabbitmq.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class ConsistentHashConsumer {

  private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> result = new ConcurrentHashMap<>();
  private final Random random = new Random();

  public void consume(final Message message) throws InterruptedException {
    Thread.sleep(random.nextInt(1, 1000));

    result.putIfAbsent(message.getBody(), new ConcurrentLinkedQueue<>());
    result.get(message.getBody()).add(message.getOrder());

    log.info(message.toString());

    assertOrdering(result);
  }

  private void assertOrdering(ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> result) {
    for (Entry<String, ConcurrentLinkedQueue<Integer>> entry : result.entrySet()) {

      final List<Integer> sorted = new ArrayList<>(entry.getValue()).stream().sorted().toList();

      Assert.isTrue(
          sorted.equals(new ArrayList<>(entry.getValue())),
          String.format("%s is not equals to %s", sorted, entry.getValue())
      );
    }
  }
}
