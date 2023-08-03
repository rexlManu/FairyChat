package de.rexlmanu.fairychat.plugin.core.playerchat;

import static de.rexlmanu.fairychat.plugin.Constants.MESSAGING_CHANNEL;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.PlayerChatCooldownService;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerChatListener implements Listener {
  private final PlayerChatFormatRenderer renderer;
  private final RedisConnector redisConnector;
  private final UserIgnoreService userIgnoreService;
  private final PlayerChatCooldownService playerChatCooldownService;
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void handle(AsyncChatEvent event) {
    if(event.isCancelled()) return;

     Player player = event.getPlayer();
    if (this.playerChatCooldownService.enabled()
        && this.playerChatCooldownService.isCooldowned(player.getUniqueId())) {
      player.sendMessage(
          this.miniMessage.deserialize(
              this.configurationProvider.get().messages().onCooldown(),
              Formatter.number(
                  "time", this.playerChatCooldownService.getTime(player.getUniqueId()) / 1000D)));
      event.setCancelled(true);
      return;
    }

    event.renderer(this.renderer);
    event
        .viewers()
        .removeIf(
            audience ->
                audience instanceof Player recipient
                    && userIgnoreService.isIgnored(recipient.getUniqueId(), player.getUniqueId()));

    if (this.playerChatCooldownService.enabled()) {
      this.playerChatCooldownService.trigger(player.getUniqueId());
    }

    // If the redis connector is not available, we don't need to do anything.
    if (!this.redisConnector.available()) return;

    Component formattedMessage = this.renderer.formatMessage(player, event.message());

    this.redisConnector.publish(
        MESSAGING_CHANNEL,
        new PlayerChatMessageData(
            Constants.SERVER_IDENTITY_ORIGIN, player.getUniqueId(), formattedMessage));
  }
}
