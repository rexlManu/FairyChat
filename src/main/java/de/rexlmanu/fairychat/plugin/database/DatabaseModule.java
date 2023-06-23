package de.rexlmanu.fairychat.plugin.database;

import com.google.inject.AbstractModule;
import de.rexlmanu.fairychat.plugin.configuration.DatabaseConfig;
import de.rexlmanu.fairychat.plugin.database.mysql.MySQLClient;
import de.rexlmanu.fairychat.plugin.database.sqlite.SQLiteClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatabaseModule extends AbstractModule {
  private final DatabaseConfig databaseConfig;

  @Override
  protected void configure() {
    if (databaseConfig.mysql()) {
      this.bind(DatabaseClient.class).to(MySQLClient.class);
    } else {
      this.bind(DatabaseClient.class).to(SQLiteClient.class);
    }
  }
}
