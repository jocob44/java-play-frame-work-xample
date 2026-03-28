package startup;

import com.typesafe.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.flywaydb.core.Flyway;

@Singleton
public class FlywayMigrationRunner {
    @Inject
    public FlywayMigrationRunner(Config config) {
        String jdbcUrl = config.getString("db.default.url");
        String user = config.getString("db.default.username");
        String password = config.getString("db.default.password");

        Flyway flyway = Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(jdbcUrl, user, password)
                .load();

        flyway.migrate();
    }
}
