package miguel.oliveira.demo.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SnapshotService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotService.class);

  private static final String QUERY = "COPY (\n"
      + "  SELECT entity.*\n"
      + "  FROM my_entity_aud entity\n"
      + "  WHERE entity.revtype <> 2\n"
      + "    AND entity.modified_at <= '%s'\n"
      + "    AND entity.modified_at=(SELECT MAX(entity_1.modified_at)\n"
      + "    FROM my_entity_aud entity_1\n"
      + "    WHERE entity_1.modified_at <= '%s' and entity_1.id=entity.id)\n"
      + "  ORDER BY entity.rev ASC"
      + ") TO '%s' (format csv, delimiter ';');";

  private final DataSource dataSource;

  public void snapshot(Instant time) throws SQLException {
    final Connection connection = dataSource.getConnection();
    try {
      connection.setAutoCommit(false);
      final Timestamp timestamp = Timestamp.from(time);
      final String query = String.format(QUERY, timestamp, timestamp,
          "C:\\Users\\Public\\dump.csv");
      final Resource resource = new ByteArrayResource(query.getBytes());
      ScriptUtils.executeSqlScript(connection, resource);
    } catch (Exception e) {
      LOGGER.error("Error dumping snapshot", e);
      connection.rollback();
    } finally {
      connection.close();
    }
  }

}
