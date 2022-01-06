package miguel.oliveira.demo.jpa;

import java.sql.Connection;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class CleanUpRepository {

  //@formatter:off
  private static final String CLEAN_UP_QUERY =
      "DO ' DECLARE"
    + "  r RECORD;"
    + "BEGIN"
    + "  FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = current_schema()) LOOP"
    + "    EXECUTE ''TRUNCATE TABLE '' || quote_ident(r.tablename) || '' CASCADE'';"
    + "  END LOOP;"
    + "END ';";
  //@formatter:on

  private final DataSource dataSource;

  public void cleanUp() {
    try (Connection connection = dataSource.getConnection()) {

      final Resource resource = new ByteArrayResource(CLEAN_UP_QUERY.getBytes());

      ScriptUtils.executeSqlScript(connection, resource);

    } catch (Exception e) {
      log.error("Error cleaning up database", e);
    }
  }
}
