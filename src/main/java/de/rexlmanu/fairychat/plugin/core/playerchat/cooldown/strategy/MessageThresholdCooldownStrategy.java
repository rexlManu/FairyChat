package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MessageThresholdCooldownStrategy
    implements CooldownStrategy<MessageThresholdCooldownStrategy.State> {
  private final Provider<PluginConfiguration> configurationProvider;

  @Override
  public boolean throttled(UUID playerId, @Nullable MessageThresholdCooldownStrategy.State state) {
    return state != null
        && state.amount >= this.configurationProvider.get().messageThresholdAmount();
  }

  @Override
  public State nextMessage(
      UUID playerId,
      Component message,
      @Nullable MessageThresholdCooldownStrategy.State previousState) {
    return new State(
        this.configurationProvider.get().messageThresholdCooldown() * 1000L,
        previousState == null ? 1 : previousState.amount + 1);
  }

  @Override
  public State deserialize(String serialized) {
    String[] parts = serialized.split(";");
    return new State(Long.parseLong(parts[0]), Integer.parseInt(parts[1]));
  }

  record State(long currentCooldown, int amount) implements CooldownState {
    @Override
    public long getCurrentCooldown() {
      return this.currentCooldown;
    }

    @Override
    public String serialize() {
      return this.currentCooldown + ";" + this.amount;
    }
  }
}
