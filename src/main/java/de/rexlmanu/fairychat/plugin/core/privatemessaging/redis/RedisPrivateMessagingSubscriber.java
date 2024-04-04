package de.rexlmanu.fairychat.plugin.core.privatemessaging.redis;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.redis.channel.RedisChannelSubscriber;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Server;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisPrivateMessagingSubscriber implements RedisChannelSubscriber<PrivateMessageData> {
  private final UserService userService;
  private final MiniMessage miniMessage;
  private final Provider<PluginConfiguration> configurationProvider;
  private final Server server;
  private final BukkitAudiences bukkitAudiences;

  @Override
  public Class<PrivateMessageData> getDataType() {
    return PrivateMessageData.class;
  }

  @Override
  public void handle(PrivateMessageData data) {
    // If the message is not for this server, we don't handle it.
    if (!data.destination().equals(Constants.SERVER_IDENTITY_ORIGIN)) return;
    User recipient = this.userService.findUserById(data.recipientId()).orElse(null);
    User sender = this.userService.findUserById(data.senderId()).orElse(null);
    // If the sender or recipient is not online, we don't handle it.
    if (recipient == null || sender == null) {
      return;
    }

    this.getPlayer(recipient)
        .ifPresent(
            player ->
                this.bukkitAudiences.player(player).sendMessage(
                    this.miniMessage.deserialize(
                        this.configurationProvider.get().privateMessaging().receiverFormat(),
                        Placeholder.unparsed("message", data.message()),
                        Placeholder.unparsed("sender_name", sender.username()),
                        Placeholder.unparsed("recipient_name", recipient.username()))));
  }

  public Optional<Player> getPlayer(User user) {
    return Optional.ofNullable(this.server.getPlayer(user.uniqueId()));
  }
}
