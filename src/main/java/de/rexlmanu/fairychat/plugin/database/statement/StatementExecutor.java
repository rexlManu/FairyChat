package de.rexlmanu.fairychat.plugin.database.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents a function that accepts a prepared statement and produces a result.
 *
 * @param <T> the result type of the function.
 */
@FunctionalInterface
public interface StatementExecutor<T> {
  T apply(PreparedStatement statement) throws SQLException;
}
