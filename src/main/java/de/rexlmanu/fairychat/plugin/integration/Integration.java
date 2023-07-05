package de.rexlmanu.fairychat.plugin.integration;

public interface Integration {
  boolean available();

  default void enable() {}
  ;
}
