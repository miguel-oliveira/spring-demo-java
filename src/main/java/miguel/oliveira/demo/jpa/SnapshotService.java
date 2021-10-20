package miguel.oliveira.demo.jpa;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
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
      + "  FROM %1$s AS entity\n"
      + "  INNER JOIN\n"
      + "  (\n"
      + "    SELECT latest_rev.id, MAX(latest_rev.rev)\n"
      + "    FROM\n"
      + "    (\n"
      + "      SELECT entity_1.id, entity_1.rev, MAX(ri.revtstmp)\n"
      + "      FROM %1$s AS entity_1\n"
      + "      INNER JOIN revinfo AS ri ON entity_1.rev = ri.rev AND entity_1.revtype <> 2 AND ri.revtstmp <= %2$d\n"
      + "      GROUP BY entity_1.id, entity_1.rev\n"
      + "    ) latest_rev\n"
      + "    GROUP BY latest_rev.id\n"
      + "  ) grouped_latest_revs ON entity.rev = grouped_latest_revs.max AND entity.id = grouped_latest_revs.id\n"
      + ") TO STDOUT (FORMAT csv, DELIMITER ';');";

  private final DataSource dataSource;

  public void snapshot(Long time) {
    final Path path = Paths.get("dump.csv");
    try (Connection connection = dataSource.getConnection();
        BaseConnection baseConnection =
            (BaseConnection) DriverManager.getConnection(connection.getMetaData().getURL());
        BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

      final String query = String.format(QUERY, "my_entity_aud", time);

      final CopyManager copyManager = new CopyManager(baseConnection);

      copyManager.copyOut(query, bufferedWriter);

    } catch (Exception e) {
      LOGGER.error("Error dumping db contents to file", e);
    }
  }
}
