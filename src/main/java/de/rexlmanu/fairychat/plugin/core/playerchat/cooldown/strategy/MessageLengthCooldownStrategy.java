package de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.strategy;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Nullable;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MessageLengthCooldownStrategy
    implements CooldownStrategy<MessageLengthCooldownStrategy.State> {

  private final Provider<PluginConfiguration> configurationProvider;

  @Override
  public boolean throttled(UUID playerId, @Nullable MessageLengthCooldownStrategy.State state) {
    return state != null;
  }

  @Override
  public State nextMessage(
      UUID playerId,
      Component message,
      @Nullable MessageLengthCooldownStrategy.State previousState) {
    String plainText = PlainTextComponentSerializer.plainText().serialize(message);

    long cooldown =
        (long) (this.configurationProvider.get().messageLengthMultiplier() * plainText.length());

    return new State(cooldown * 1000L);
  }

  @Override
  public State deserialize(String serialized) {
    return new State(Long.parseLong(serialized));
  }

  record State(long currentCooldown) implements CooldownState {
    @Override
    public long getCurrentCooldown() {
      return this.currentCooldown;
    }

    @Override
    public String serialize() {
      return String.valueOf(this.currentCooldown);
    }
  }
}
