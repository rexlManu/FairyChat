package de.rexlmanu.fairychat.plugin.core.privatemessaging.redis;

import static de.rexlmanu.fairychat.plugin.Constants.LAST_RECIPIENTS_KEY;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.PrivateMessagingService;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Server;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisPrivateMessagingService implements PrivateMessagingService {
  private final RedisConnector connector;
  private final UserService userService;
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;
  private final Server server;

  public void setLastRecipient(UUID senderId, UUID recipientId) {
    this.connector.useResourceAsync(
        jedis -> {
          jedis.hset(LAST_RECIPIENTS_KEY, senderId.toString(), recipientId.toString());
          jedis.expire(
              LAST_RECIPIENTS_KEY,
              this.configurationProvider.get().privateMessaging().recipientExpirationSeconds());
        });
  }

  @Override
  public void sendMessage(User user, User recipient, String message) {
    this.setLastRecipient(recipient.uniqueId(), user.uniqueId());

    this.getPlayer(user)
        .ifPresent(
            player ->
                player.sendMessage(
                    this.miniMessage.deserialize(
                        this.configurationProvider.get().privateMessaging().senderFormat(),
                        Placeholder.unparsed("message", message),
                        Placeholder.unparsed("sender_name", user.username()),
                        Placeholder.unparsed("recipient_name", recipient.username()))));

    // If the recipient is on the same server, we send directly to the player.
    // Otherwise, we send the message over redis to the recipient.
    this.getPlayer(recipient)
        .ifPresentOrElse(
            player ->
                player.sendMessage(
                    this.miniMessage.deserialize(
                        this.configurationProvider.get().privateMessaging().receiverFormat(),
                        Placeholder.unparsed("message", message),
                        Placeholder.unparsed("sender_name", user.username()),
                        Placeholder.unparsed("recipient_name", recipient.username()))),
            () ->
                this.connector.publish(
                    Constants.PRIVATE_MESSAGING_CHANNEL,
                    new PrivateMessageData(
                        user.serverIdentity(),
                        recipient.serverIdentity(),
                        user.uniqueId(),
                        recipient.uniqueId(),
                        message)));
  }

  @Override
  public Optional<User> lastRecipient(User user) {
    return this.connector
        .<Optional<UUID>>useQuery(
            jedis -> {
              String content = jedis.hget(LAST_RECIPIENTS_KEY, user.uniqueId().toString());
              if (content == null) return Optional.empty();

              return Optional.of(UUID.fromString(content));
            })
        .flatMap(this.userService::findUserById);
  }

  public Optional<Player> getPlayer(User user) {
    return Optional.ofNullable(this.server.getPlayer(user.uniqueId()));
  }
}
