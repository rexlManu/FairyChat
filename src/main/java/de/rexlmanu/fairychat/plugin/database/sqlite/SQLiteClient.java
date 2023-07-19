package de.rexlmanu.fairychat.plugin.database.sqlite;

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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class SQLiteClient implements DatabaseClient, DatabaseQueries {
  @Named("dataFolder")
  private final Path dataDirectory;

  private final Provider<PluginConfiguration> configurationProvider;

  private Path databaseFile;
  private HikariDataSource dataSource;

  @SneakyThrows
  @Override
  public void open() {
    this.databaseFile = this.dataDirectory.resolve("database.db");

    if (!Files.exists(this.databaseFile)) {
      Files.createFile(this.databaseFile);
    }

    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setPoolName("FairyChat");
    hikariConfig.setDriverClassName("org.sqlite.JDBC");
    hikariConfig.setJdbcUrl("jdbc:sqlite:" + this.databaseFile.toAbsolutePath());
    hikariConfig.setConnectionTestQuery("SELECT 1");
    hikariConfig.setMaxLifetime(60000);
    hikariConfig.setIdleTimeout(45000);
    hikariConfig.setMaximumPoolSize(50);
    this.dataSource = new HikariDataSource(hikariConfig);

    this.createTables();
  }

  @Override
  public void close() {
    this.dataSource.close();
  }

  @Override
  public boolean available() {
    throw new UnsupportedOperationException("Not supported.");
  }

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
  public void createTables() {
    this.newBuilder(
            "CREATE TABLE IF NOT EXISTS %s (user_id TEXT NOT NULL, target_id TEXT NOT NULL, PRIMARY KEY (user_id, target_id));"
                .formatted(this.configurationProvider.get().database().playerIgnoreTable()))
        .execute();
  }

  @Override
  public String selectUserIgnore() {
    return "SELECT * FROM %s WHERE user_id = ? AND target_id = ? LIMIT 1;"
        .formatted(this.configurationProvider.get().database().playerIgnoreTable());
  }

  @Override
  public String insertUserIgnore() {
    return "INSERT INTO %s (user_id, target_id) VALUES (?, ?);"
        .formatted(this.configurationProvider.get().database().playerIgnoreTable());
  }

  @Override
  public String deleteUserIgnore() {
    return "DELETE FROM %s WHERE user_id = ? AND target_id = ?;"
        .formatted(this.configurationProvider.get().database().playerIgnoreTable());
  }
}
