package de.rexlmanu.fairychat.plugin.utility;

import java.util.Objects;
import java.util.UUID;

public record ServerIdentity(UUID identifier) {
  public static ServerIdentity random() {
    return new ServerIdentity(UUID.randomUUID());
  }

  public static ServerIdentity from(UUID identifier) {
    return new ServerIdentity(identifier);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ServerIdentity serverIdentity = (ServerIdentity) o;

    return Objects.equals(identifier, serverIdentity.identifier);
  }

  @Override
  public int hashCode() {
    return identifier != null ? identifier.hashCode() : 0;
  }
}
