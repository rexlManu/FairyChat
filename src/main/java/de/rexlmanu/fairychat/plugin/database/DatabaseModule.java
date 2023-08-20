package de.rexlmanu.fairychat.plugin.database;

import com.google.inject.AbstractModule;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfigurationProvider;
import de.rexlmanu.fairychat.plugin.database.mysql.MySQLClient;
import de.rexlmanu.fairychat.plugin.database.sqlite.SQLiteClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatabaseModule extends AbstractModule {
  private final PluginConfigurationProvider configurationProvider;

  @Override
  protected void configure() {
    if (this.configurationProvider.configuration().database().mysql()) {
      this.bind(DatabaseClient.class).to(MySQLClient.class);
    } else {
      this.bind(DatabaseClient.class).to(SQLiteClient.class);
    }
  }
}
