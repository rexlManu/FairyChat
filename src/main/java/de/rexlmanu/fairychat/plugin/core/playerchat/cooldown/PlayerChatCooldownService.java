package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown;

import java.util.UUID;

public interface PlayerChatCooldownService {
  void trigger(UUID playerId);

  boolean isCooldowned(UUID playerId);

  long getTime(UUID playerId);

  boolean enabled();
}
