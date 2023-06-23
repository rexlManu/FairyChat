package de.rexlmanu.fairychat.plugin.database;

import de.rexlmanu.fairychat.plugin.database.statement.StatementBuilder;

public interface DatabaseClient extends Connector {
  StatementBuilder newBuilder(String statement);

  DatabaseQueries queries();
}
