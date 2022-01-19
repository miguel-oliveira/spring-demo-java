package miguel.oliveira.demo.jpa;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SnapshotService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotService.class);

  private static final String COLUMN_LIST_QUERY =
      //@formatter:off
        "COPY ("
      + "  SELECT array_to_string(ARRAY("
      + "    SELECT 'entity' || '.' || c.column_name"
      + "    FROM information_schema.columns As c"
      + "    WHERE table_name = '%1$s'"
      + "    AND c.column_name NOT IN('rev', 'revtype')"
      + "  ), ',')"
      + ") TO STDOUT";
      //@formatter:on

  private static final String QUERY =
      //@formatter:off
        "COPY ("
      + "  SELECT %1$s"
      + "  FROM %2$s AS entity"
      + "  INNER JOIN"
      + "  ("
      + "    SELECT latest_rev.id, MAX(latest_rev.rev)"
      + "    FROM"
      + "    ("
      + "      SELECT entity_1.id, entity_1.rev"
      + "      FROM %2$s AS entity_1"
      + "      INNER JOIN revinfo AS ri ON entity_1.rev = ri.rev AND ri.revtstmp <= %3$d"
      + "    ) latest_rev"
      + "    GROUP BY latest_rev.id"
      + "  ) grouped_latest_revs ON entity.rev = grouped_latest_revs.max AND entity.id = grouped_latest_revs.id AND entity.revtype <> 2"
      + ") TO STDOUT (FORMAT CSV, HEADER);";
      //@formatter:on

  private final DataSource dataSource;

  public void snapshot(Long time) {
    final Path path = Paths.get("dump.csv");
    try (Connection connection = dataSource.getConnection();
        Writer columnList = new StringWriter();
        BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

      final PGConnection pgConnection = connection.unwrap(PGConnection.class);

      final CopyManager copyManager = pgConnection.getCopyAPI();

      final String columnListQuery = String.format(COLUMN_LIST_QUERY, "my_entity_aud");

      copyManager.copyOut(columnListQuery, columnList);

      final String query = String.format(QUERY, columnList, "my_entity_aud", time);

      copyManager.copyOut(query, bufferedWriter);

    } catch (Exception e) {
      LOGGER.error("Error dumping db contents to file", e);
    }
  }
}
