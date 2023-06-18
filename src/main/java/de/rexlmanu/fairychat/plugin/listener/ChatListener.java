package de.rexlmanu.fairychat.plugin.listener;

import static de.rexlmanu.fairychat.plugin.Constants.MESSAGING_CHANNEL;

import com.google.inject.Inject;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import de.rexlmanu.fairychat.plugin.redis.data.PlayerChatMessageData;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class ChatListener implements Listener {
  private final FormatChatRenderer renderer;
  private final RedisConnector redisConnector;

  @EventHandler(priority = EventPriority.MONITOR)
  public void handle(AsyncChatEvent event) {
    event.renderer(this.renderer);

    // If the redis connector is not available, we don't need to do anything.
    if (!this.redisConnector.available()) return;

    Component formattedMessage = this.renderer.formatMessage(event.getPlayer(), event.message());

    this.redisConnector.publish(
        MESSAGING_CHANNEL,
        new PlayerChatMessageData(
            Constants.SERVER_ID, event.getPlayer().getUniqueId(), formattedMessage));
  }
}
