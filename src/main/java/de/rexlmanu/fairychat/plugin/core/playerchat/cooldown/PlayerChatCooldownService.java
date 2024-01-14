package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown;

import java.util.UUID;
import net.kyori.adventure.text.Component;

public interface PlayerChatCooldownService {
  void trigger(UUID playerId, Component message);

  boolean isCooldowned(UUID playerId);

  long getTime(UUID playerId);

  boolean enabled();
}
