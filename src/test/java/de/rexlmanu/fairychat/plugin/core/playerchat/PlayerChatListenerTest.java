package de.rexlmanu.fairychat.plugin.core.playerchat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.ignore.UserIgnoreService;
import de.rexlmanu.fairychat.plugin.core.playerchat.cooldown.PlayerChatCooldownService;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.junit.jupiter.api.Test;

class PlayerChatListenerTest {
  private final Player player = mock(Player.class);
  private final PlayerChatFormatRenderer renderer = mock(PlayerChatFormatRenderer.class);
  private final RedisConnector redis = mock(RedisConnector.class);
  private final UserIgnoreService ignores = mock(UserIgnoreService.class);
  private final PlayerChatCooldownService cooldown = mock(PlayerChatCooldownService.class);
  private final PlayerChatListener listener =
      new PlayerChatListener(
          renderer, redis, ignores, cooldown, PluginConfiguration::new, MiniMessage.miniMessage());

  private AsyncChatEvent event() {
    when(player.getUniqueId()).thenReturn(UUID.randomUUID());
    Component message = Component.text("hello");
    return new AsyncChatEvent(
        true,
        player,
        new HashSet<>(),
        ChatRenderer.defaultRenderer(),
        message,
        message,
        SignedMessage.system("hello", message));
  }

  // Use Paper's cancellation handling for each real event handler.
  private void dispatch(String methodName, AsyncChatEvent event) throws Exception {
    var method = PlayerChatListener.class.getMethod(methodName, AsyncChatEvent.class);
    var handler = method.getAnnotation(EventHandler.class);
    new RegisteredListener(
            listener,
            (target, input) -> {
              try {
                method.invoke(target, input);
              } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
              }
            },
            handler.priority(),
            mock(Plugin.class),
            handler.ignoreCancelled())
        .callEvent(event);
  }

  @Test
  void previouslyCancelledChatHasNoSideEffects() throws Exception {
    var event = event();
    event.setCancelled(true);
    dispatch("handle", event);
    dispatch("handleAcceptedMessage", event);
    verifyNoInteractions(redis, cooldown, renderer, ignores);
  }

  @Test
  void cancellationByALaterPluginPreventsRedisAndCooldown() throws Exception {
    var event = event();
    when(cooldown.enabled()).thenReturn(true);
    dispatch("handle", event);
    event.setCancelled(true);
    dispatch("handleAcceptedMessage", event);
    verify(cooldown, never()).trigger(any(), any());
    verifyNoInteractions(redis);
  }

  @Test
  void ignoredViewersAreRemovedAndAcceptedChatIsPublishedOnce() throws Exception {
    var event = event();
    Player ignored = mock(Player.class);
    Player recipient = mock(Player.class);
    UUID ignoredId = UUID.randomUUID();
    when(ignored.getUniqueId()).thenReturn(ignoredId);
    when(recipient.getUniqueId()).thenReturn(UUID.randomUUID());
    when(ignores.isIgnored(ignoredId, player.getUniqueId())).thenReturn(true);
    event.viewers().addAll(Set.of(ignored, recipient));
    when(redis.available()).thenReturn(true);
    Component formatted = Component.text("player: hello");
    when(renderer.formatMessage(player, event.message())).thenReturn(formatted);

    dispatch("handle", event);
    assertEquals(Set.of(recipient), event.viewers());
    assertSame(renderer, event.renderer());
    verifyNoInteractions(redis);
    dispatch("handleAcceptedMessage", event);
    verify(redis)
        .publish(
            Constants.MESSAGING_CHANNEL,
            new PlayerChatMessageData(
                Constants.SERVER_IDENTITY_ORIGIN, player.getUniqueId(), formatted));
  }

  @Test
  void cooldownCancelsChatBeforeDelivery() throws Exception {
    var event = event();
    when(cooldown.enabled()).thenReturn(true);
    when(cooldown.isCooldowned(player.getUniqueId())).thenReturn(true);
    dispatch("handle", event);
    dispatch("handleAcceptedMessage", event);
    assertTrue(event.isCancelled());
    verifyNoInteractions(redis, renderer);
    verify(cooldown, never()).trigger(any(), any());
  }
}
