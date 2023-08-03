package de.rexlmanu.fairychat.plugin.core.messagesimilarity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.utility.ExpiringMap;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class MessageSimilarityBukkitListener implements Listener {
  private final ExpiringMap<UUID, String> lastMessages = new ExpiringMap<>();
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;

  @EventHandler(priority = EventPriority.NORMAL)
  public void handle(AsyncChatEvent event) {
    Player player = event.getPlayer();
    if (this.lastMessages.containsKey(player.getUniqueId())) {
      String lastMessage = this.lastMessages.get(player.getUniqueId());
      String currentMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

      double score =
          configurationProvider
              .get()
              .similarityAlgorithm()
              .strategy()
              .get()
              .score(currentMessage, lastMessage);

      if (score >= configurationProvider.get().similarityPercentage()) {
        event.setCancelled(true);
        player.sendMessage(
            this.miniMessage.deserialize(
                configurationProvider.get().messages().yourLastMessageWasTooSimilar()));
        return;
      }
    }

    this.lastMessages.put(
        player.getUniqueId(),
        PlainTextComponentSerializer.plainText().serialize(event.message()),
        this.configurationProvider.get().similarityMessageCacheSeconds(),
        TimeUnit.SECONDS);
  }
}
