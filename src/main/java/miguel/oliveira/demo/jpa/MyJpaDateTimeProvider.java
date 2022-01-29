package miguel.oliveira.demo.jpa;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

@Component(MyJpaDateTimeProvider.NAME)
@AllArgsConstructor
public class MyJpaDateTimeProvider implements DateTimeProvider {

  public static final String NAME = "myJpaDateTimeProvider";

  private final MyContextHolder myContextHolder;

  @Override
  @NonNull
  public Optional<TemporalAccessor> getNow() {
    final Long timestamp = myContextHolder.getTimestamp();
    if (timestamp != null) {
      return Optional.of(Instant.ofEpochMilli(timestamp));
    } else {
      return Optional.of(Instant.now().truncatedTo(ChronoUnit.MILLIS));
    }
  }
}
