package miguel.oliveira.demo.jpa;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

//@Component(MyDateTimeProvider.NAME)
public class MyDateTimeProvider implements DateTimeProvider {

  public static final String NAME = "myDateTimeProvider";

  @Override
  @NonNull
  public Optional<TemporalAccessor> getNow() {
    return Optional.of(Instant.now().truncatedTo(ChronoUnit.MILLIS));
  }
}
