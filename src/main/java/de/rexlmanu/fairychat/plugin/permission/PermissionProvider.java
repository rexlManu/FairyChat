package de.rexlmanu.fairychat.plugin.permission;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PermissionProvider {
  @Nullable String getGroup(@NotNull UUID playerId);
}
