package de.rexlmanu.fairychat.plugin.core.ignore;

import com.google.inject.ImplementedBy;
import java.util.UUID;

@ImplementedBy(DefaultUserIgnoreService.class)
public interface UserIgnoreService {
  boolean isIgnored(UUID userId, UUID targetId);

  void setIgnored(UUID userId, UUID targetId, boolean ignored);
}
