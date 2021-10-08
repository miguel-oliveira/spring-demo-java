package miguel.oliveira.demo.jpa;

import java.sql.SQLException;
import java.time.Instant;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SnapshotService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotService.class);

  private DataSource dataSource;

  public void snapshot(Instant time) {
    try {
      // TODO: Find a way to pass time to script
      ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("dump.sql"));
    } catch (SQLException e) {
      LOGGER.error("Error dumping snapshot", e);
    }
  }

}
