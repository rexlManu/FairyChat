package de.rexlmanu.fairychat.plugin.core.custommessages;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.configuration.records.Messages;
import de.rexlmanu.fairychat.plugin.integration.IntegrationRegistry;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.Test;

class CustomMessageBukkitListenerTest {
  private final Player player = mock(Player.class);
  private final RedisConnector redis = mock(RedisConnector.class);
  private final PluginConfiguration configuration = spy(new PluginConfiguration());
  private final CustomMessageBukkitListener listener =
      new CustomMessageBukkitListener(
          MiniMessage.miniMessage(), mock(IntegrationRegistry.class), () -> configuration, redis);

  @Test
  void joinPermissionSuppressesTheNativeMessage() {
    when(player.hasPermission("fairychat.messages.join.ignore")).thenReturn(true);
    var event = new PlayerJoinEvent(player, Component.text("joined"));
    listener.handlePlayerJoin(event);
    assertNull(event.joinMessage());
    verifyNoInteractions(redis);
  }

  @Test
  void quitPermissionSuppressesTheNativeMessage() {
    when(player.hasPermission("fairychat.messages.quit.ignore")).thenReturn(true);
    var event =
        new PlayerQuitEvent(
            player, Component.text("left"), PlayerQuitEvent.QuitReason.DISCONNECTED);
    listener.handlePlayerQuit(event);
    assertNull(event.quitMessage());
    verifyNoInteractions(redis);
  }

  @Test
  void emptyFormatSuppressesTheNativeMessage() {
    Messages messages = mock(Messages.class);
    when(messages.joinMessage()).thenReturn("");
    when(configuration.messages()).thenReturn(messages);
    var event = new PlayerJoinEvent(player, Component.text("joined"));
    listener.handlePlayerJoin(event);
    assertNull(event.joinMessage());
    verifyNoInteractions(redis);
  }

  @Test
  void messageSuppressedByAnotherPluginStaysSuppressed() {
    var event = new PlayerJoinEvent(player, (Component) null);
    listener.handlePlayerJoin(event);
    assertNull(event.joinMessage());
    verifyNoInteractions(redis);
  }
}
