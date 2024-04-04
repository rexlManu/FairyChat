package de.rexlmanu.fairychat.plugin.paper;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PaperAsyncChatHandler implements Listener {
  @RequiredArgsConstructor
  @Data
  public static class WrappedAsyncChatEvent {
    private final AsyncChatEvent event;

    public Player getPlayer() {
      return this.event.getPlayer();
    }

    public void setCancelled(boolean cancelled) {
      this.event.setCancelled(cancelled);
    }

    public void renderer(DefaultChatRenderer renderer) {
      this.event.renderer(renderer::render);
    }

    public @NotNull Set<Audience> viewers() {
      return this.event.viewers();
    }

    public Component message() {
      return this.event.message();
    }
  }

  private final Consumer<WrappedAsyncChatEvent> eventConsumer;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void handle(AsyncChatEvent event) {
    this.eventConsumer.accept(new WrappedAsyncChatEvent(event));
  }
}
