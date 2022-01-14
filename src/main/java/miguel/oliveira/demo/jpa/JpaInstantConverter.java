package miguel.oliveira.demo.jpa;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class JpaInstantConverter implements AttributeConverter<Instant, Timestamp> {

  @Override
  public Timestamp convertToDatabaseColumn(Instant instant) {
    return instant != null ? Timestamp.from(instant.truncatedTo(ChronoUnit.MILLIS)) : null;
  }

  @Override
  public Instant convertToEntityAttribute(Timestamp timestamp) {
    return timestamp != null ? timestamp.toInstant() : null;
  }
}
