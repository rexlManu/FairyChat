package de.rexlmanu.fairychat.plugin.database.statement;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a function that accepts a result set and produces a result. The resolver is mostly
 * used to map the result set to an object.
 *
 * @param <T> the result type of the function.
 */
public interface StatementResolver<T> {
  T resolve(ResultSet resultSet) throws SQLException;
}
