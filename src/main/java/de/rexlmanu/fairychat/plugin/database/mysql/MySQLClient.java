package de.rexlmanu.fairychat.plugin.database.mysql;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.database.DatabaseClient;
import de.rexlmanu.fairychat.plugin.database.DatabaseQueries;
import de.rexlmanu.fairychat.plugin.database.statement.StatementBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/** The database client is used to create a connection to the database with hikari. */
@Singleton
public class MySQLClient implements DatabaseClient, DatabaseQueries {
  private final Provider<PluginConfiguration> configurationProvider;
  private final HikariConfig hikariConfig;
  @Getter private HikariDataSource dataSource;

  @Inject
  public MySQLClient(
      @Named("dataFolder") Path dataDirectory, Provider<PluginConfiguration> configurationProvider, JavaPlugin plugin) {
    this.configurationProvider = configurationProvider;
    this.hikariConfig = this.createConfig(dataDirectory);

    this.hikariConfig.setPoolName(plugin.getName());
  }

  @Override
  public boolean available() {
    throw new UnsupportedOperationException("Not supported.");
  }

  private HikariConfig createConfig(Path dataDirectory) {
    // We support a properties file for more customization.
    if (Files.exists(dataDirectory.resolve("mysql.properties"))) {
      return new HikariConfig(dataDirectory.resolve("mysql.properties").toString());
    }

    HikariConfig createdConfig = new HikariConfig();
    createdConfig.setJdbcUrl(this.configurationProvider.get().database().url());
    createdConfig.setUsername(this.configurationProvider.get().database().username());
    createdConfig.setPassword(this.configurationProvider.get().database().password());
    return createdConfig;
  }

  /**
   * Create a new statement builder.
   *
   * @param statement the statement.
   * @return the statement builder.
   */
  public StatementBuilder newBuilder(String statement) {
    try {
      return new StatementBuilder(this.dataSource.getConnection())
          .statement(statement)
          .logging(true);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public DatabaseQueries queries() {
    return this;
  }

  @Override
  public void open() {
    this.dataSource = new HikariDataSource(this.hikariConfig);

    this.createTables();
  }

  public void close() {
    if (this.dataSource == null) return;
    this.dataSource.close();
  }

  @Override
  public void createTables() {
    this.newBuilder(
            "CREATE TABLE IF NOT EXISTS `%s` (`user_id` VARCHAR(36) NOT NULL, `target_id` VARCHAR(36) NOT NULL, PRIMARY KEY (`user_id`, `target_id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8"
                .formatted(this.configurationProvider.get().database().playerIgnoreTable()))
        .execute();
  }

  @Override
  public String selectUserIgnore() {
    return "SELECT * FROM `%s` WHERE `user_id` = ? AND `target_id` = ? LIMIT 1;"
        .formatted(this.configurationProvider.get().database().playerIgnoreTable());
  }

  @Override
  public String insertUserIgnore() {
    return "INSERT INTO `%s` (`user_id`, `target_id`) VALUES (?, ?);"
        .formatted(this.configurationProvider.get().database().playerIgnoreTable());
  }

  @Override
  public String deleteUserIgnore() {
    return "DELETE FROM `%s` WHERE `user_id` = ? AND `target_id` = ?;"
        .formatted(this.configurationProvider.get().database().playerIgnoreTable());
  }
}
