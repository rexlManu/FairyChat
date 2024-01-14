package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

/** Represents a strategy for calculating the cooldown for a message. */
public interface CooldownStrategy<S extends CooldownStrategy.CooldownState> {
  boolean throttled(UUID playerId, @Nullable S state);
  S nextMessage(UUID playerId, Component message, @Nullable S previousState);

  S deserialize(String serialized);

  interface CooldownState {
    long getCurrentCooldown();

    String serialize();
  }
}
