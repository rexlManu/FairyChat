package de.rexlmanu.fairychat.plugin.database;

public interface Connector {
  void open();

  void close();

  boolean available();
}
