package de.rexlmanu.fairychat.plugin.core.privatemessaging.defaults;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.PrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.utility.ExpiringMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultPrivateMessagingService implements PrivateMessagingService {
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;
  private final Server server;
  private final UserService userService;
  private final ExpiringMap<UUID, UUID> lastMessageRecipient = new ExpiringMap<>();

  @Override
  public void sendMessage(User user, User recipient, String message) {
    Audience.audience(this.getPlayer(user), this.getPlayer(recipient))
        .sendMessage(
            this.miniMessage.deserialize(
                configurationProvider.get().privateMessaging().format(),
                Placeholder.unparsed("message", message),
                Placeholder.unparsed("sender_name", user.username()),
                Placeholder.unparsed("recipient_name", recipient.username())));

    this.lastMessageRecipient.put(
        recipient.uniqueId(),
        user.uniqueId(),
        configurationProvider.get().privateMessaging().recipientExpirationSeconds(),
        TimeUnit.SECONDS);
  }

  public Optional<User> lastRecipient(User user) {
    return Optional.ofNullable(this.lastMessageRecipient.get(user.uniqueId()))
        .flatMap(this.userService::findUserById);
  }

  /** We ignore the factor that the player can be null because we know that the player is online. */
  @NotNull
  public Player getPlayer(User user) {
    return Objects.requireNonNull(this.server.getPlayer(user.uniqueId()));
  }
}
