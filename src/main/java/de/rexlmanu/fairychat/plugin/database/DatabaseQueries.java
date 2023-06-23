package de.rexlmanu.fairychat.plugin.database;

public interface DatabaseQueries {
  void createTables();

  String selectUserIgnore();

  String insertUserIgnore();

  String deleteUserIgnore();
}
