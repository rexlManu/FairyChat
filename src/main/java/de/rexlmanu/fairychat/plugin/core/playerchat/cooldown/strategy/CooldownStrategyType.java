package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy;

import com.google.inject.Singleton;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public enum CooldownStrategyType {
  DISABLED(EmptyStrategy.class),
  MESSAGE_LENGTH(MessageLengthCooldownStrategy.class),
  MESSAGE_THRESHOLD(MessageThresholdCooldownStrategy.class);

  private Class<? extends CooldownStrategy<? extends CooldownStrategy.CooldownState>> strategyClass;

  @Singleton
  public static class EmptyStrategy implements CooldownStrategy<EmptyStrategy.State> {
    @Override
    public boolean throttled(UUID playerId, @Nullable State state) {
      return false;
    }

    @Override
    public State nextMessage(UUID playerId, Component message, @Nullable State previousState) {
      return new State();
    }

    @Override
    public State deserialize(String serialized) {
      return new State();
    }

    record State() implements CooldownState {
      @Override
      public long getCurrentCooldown() {
        return 0;
      }

      @Override
      public String serialize() {
        return "";
      }
    }
  }
  ;
}
