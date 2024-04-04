package de.rexlmanu.fairychat.plugin.paper.event;

import de.rexlmanu.fairychat.plugin.paper.DefaultChatRenderer;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class FairyAsyncChatEvent extends Event implements Cancellable {
  private static final HandlerList HANDLER_LIST = new HandlerList();

  private Player player;
  private Set<Audience> viewers;
  private DefaultChatRenderer renderer;
  private Component message;
  private boolean cancelled;

  public FairyAsyncChatEvent(
      final boolean async,
      final @NotNull Player player,
      final @NotNull Set<Audience> viewers,
      final @NotNull DefaultChatRenderer renderer,
      final @NotNull Component message) {
    super(async);
    this.player = player;
    this.viewers = viewers;
    this.renderer = renderer;
    this.message = message;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Override
  public boolean isCancelled() {
    return this.cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }
}
