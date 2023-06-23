package de.rexlmanu.fairychat.plugin.core.ignore;

import java.util.UUID;

public interface UserIgnoreService {
  boolean isIgnored(UUID userId, UUID targetId);

  void setIgnored(UUID userId, UUID targetId, boolean ignored);
}
