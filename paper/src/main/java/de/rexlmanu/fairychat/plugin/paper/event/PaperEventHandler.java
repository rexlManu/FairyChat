package de.rexlmanu.fairychat.plugin.paper.event;

import com.google.inject.Inject;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.HashSet;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PaperEventHandler implements FairyEventHandler {
  @EventHandler
  public void handle(AsyncChatEvent event) {
    FairyAsyncChatEvent chatEvent =
        new FairyAsyncChatEvent(
            event.isAsynchronous(),
            event.getPlayer(),
            new HashSet<>(event.viewers()),
            event.renderer()::render,
            event.message());

    chatEvent.setCancelled(event.isCancelled());

    chatEvent.callEvent();

    event.setCancelled(chatEvent.isCancelled());
    event.renderer(chatEvent.renderer()::render);
    event.message(chatEvent.message());
    event.viewers().clear();
    event.viewers().addAll(chatEvent.viewers());
  }
}
