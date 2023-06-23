package de.rexlmanu.fairychat.plugin.database.statement;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a consumer that accepts a result set.
 */
@FunctionalInterface
public interface ResultSetConsumer {
  void accept(ResultSet resultSet) throws SQLException;
}
