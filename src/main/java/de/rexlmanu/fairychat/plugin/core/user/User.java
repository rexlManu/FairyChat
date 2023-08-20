package de.rexlmanu.fairychat.plugin.core.user;

import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;
import java.util.Objects;
import java.util.UUID;

public record User(UUID uniqueId, String username, ServerIdentity serverIdentity) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;

    User user = (User) o;

    return Objects.equals(this.uniqueId, user.uniqueId);
  }

  @Override
  public int hashCode() {
    return this.uniqueId != null ? this.uniqueId.hashCode() : 0;
  }
}
