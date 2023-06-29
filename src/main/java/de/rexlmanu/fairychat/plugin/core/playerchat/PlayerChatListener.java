package de.rexlmanu.fairychat.plugin.core.playerchat;

import static de.rexlmanu.fairychat.plugin.Constants.MESSAGING_CHANNEL;

import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerChatListener implements Listener {
  private final PlayerChatFormatRenderer renderer;
  private final RedisConnector redisConnector;
  private final UserIgnoreService userIgnoreService;

  @EventHandler(priority = EventPriority.MONITOR)
  public void handle(AsyncChatEvent event) {
    event.renderer(this.renderer);
    event
        .viewers()
        .removeIf(
            audience ->
                audience instanceof Player recipient
                    && userIgnoreService.isIgnored(
                        recipient.getUniqueId(), event.getPlayer().getUniqueId()));

    // If the redis connector is not available, we don't need to do anything.
    if (!this.redisConnector.available()) return;

    Component formattedMessage = this.renderer.formatMessage(event.getPlayer(), event.message());

    this.redisConnector.publish(
        MESSAGING_CHANNEL,
        new PlayerChatMessageData(
            Constants.SERVER_IDENTITY_ORIGIN, event.getPlayer().getUniqueId(), formattedMessage));
  }
}
