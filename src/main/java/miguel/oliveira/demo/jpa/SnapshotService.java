package miguel.oliveira.demo.jpa;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.time.Instant;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SnapshotService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotService.class);

  private static final String QUERY = "COPY (\n"
      + "  SELECT entity.*\n"
      + "  FROM %1$s entity\n"
      + "  WHERE entity.revtype <> 2\n"
      + "    AND entity.modified_at <= '%2$s'\n"
      + "    AND entity.modified_at=(SELECT MAX(entity_1.modified_at)\n"
      + "      FROM %1$s entity_1\n"
      + "      WHERE entity_1.modified_at <= '%2$s' AND entity_1.id=entity.id)\n"
      + "  ORDER BY entity.rev ASC"
      + ") TO STDOUT (FORMAT csv, DELIMITER ';');";

  private final DataSource dataSource;

  public void snapshot(Instant time) {
    final Path path = Paths.get("dump.csv");
    try (Connection connection = dataSource.getConnection();
        BaseConnection baseConnection =
            (BaseConnection) DriverManager.getConnection(connection.getMetaData().getURL());
        BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

      final Timestamp timestamp = Timestamp.from(time);

      final String query = String.format(QUERY, "my_entity_aud", timestamp);

      final CopyManager copyManager = new CopyManager(baseConnection);

      copyManager.copyOut(query, bufferedWriter);

    } catch (Exception e) {
      LOGGER.error("Error dumping db contents to file", e);
    }
  }
}
