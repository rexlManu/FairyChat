package de.rexlmanu.fairychat.plugin.core.messagesimilarity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.paper.event.FairyAsyncChatEvent;
import de.rexlmanu.fairychat.plugin.utility.ExpiringMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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
  private final BukkitAudiences bukkitAudiences;

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void handle(FairyAsyncChatEvent event) {
    Player player = event.player();

    if (player.hasPermission("fairychat.bypass.similarity")) return;

    if (this.lastMessages.containsKey(player.getUniqueId())) {
      String lastMessage = this.lastMessages.get(player.getUniqueId());
      String currentMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

      double score =
          this.configurationProvider
              .get()
              .similarityAlgorithm()
              .strategy()
              .get()
              .score(currentMessage, lastMessage);

      if (score >= this.configurationProvider.get().similarityPercentage()) {
        event.setCancelled(true);
        bukkitAudiences
            .player(player)
            .sendMessage(
                this.miniMessage.deserialize(
                    this.configurationProvider.get().messages().yourLastMessageWasTooSimilar()));
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void handleChatAfterProcess(FairyAsyncChatEvent event) {
    this.lastMessages.put(
        event.player().getUniqueId(),
        PlainTextComponentSerializer.plainText().serialize(event.message()),
        this.configurationProvider.get().similarityMessageCacheSeconds(),
        TimeUnit.SECONDS);
  }
}
