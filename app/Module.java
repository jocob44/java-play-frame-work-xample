import com.google.inject.AbstractModule;
import startup.FlywayMigrationRunner;

public class Module extends AbstractModule {
    @Override
    protected void configure() {
        bind(FlywayMigrationRunner.class).asEagerSingleton();
    }
}
