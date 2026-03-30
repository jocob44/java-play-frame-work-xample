import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import startup.FlywayMigrationRunner;

import java.util.TimeZone;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        Config config = ConfigFactory.load();
        if (config.hasPath("app.timezone")) {
            String timezone = config.getString("app.timezone");
            TimeZone.setDefault(TimeZone.getTimeZone(timezone));
            System.setProperty("user.timezone", timezone);
        }

        bind(FlywayMigrationRunner.class).asEagerSingleton();
    }
}
