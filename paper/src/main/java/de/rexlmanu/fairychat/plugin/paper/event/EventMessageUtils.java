package de.rexlmanu.fairychat.plugin.paper.event;

import de.rexlmanu.fairychat.plugin.paper.Environment;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class EventMessageUtils {
  public static Supplier<Component> quitMessage(PlayerQuitEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::quitMessage;
    }
    return () -> {
      String quitMessage = event.getQuitMessage();
      if (quitMessage == null) return Component.empty();
      return LegacyComponentSerializer.legacySection().deserialize(quitMessage);
    };
  }

  public static Consumer<Component> quitMessageSetter(PlayerQuitEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::quitMessage;
    }
    return component -> {
      if (component == null) {
        event.setQuitMessage(null);
        return;
      }
      event.setQuitMessage(LegacyComponentSerializer.legacySection().serialize(component));
    };
  }

  public static Supplier<Component> joinMessage(PlayerJoinEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::joinMessage;
    }
    return () -> {
      String joinMessage = event.getJoinMessage();
      if (joinMessage == null) return Component.empty();
      return LegacyComponentSerializer.legacySection().deserialize(joinMessage);
    };
  }

  public static Consumer<Component> joinMessageSetter(PlayerJoinEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::joinMessage;
    }
    return component -> {
      if (component == null) {
        event.setJoinMessage(null);
        return;
      }
      event.setJoinMessage(LegacyComponentSerializer.legacySection().serialize(component));
    };
  }

  public static Supplier<Component> deathMessage(PlayerDeathEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::deathMessage;
    }
    return () -> {
      String deathMessage = event.getDeathMessage();
      if (deathMessage == null) return Component.empty();
      return LegacyComponentSerializer.legacySection().deserialize(deathMessage);
    };
  }

  public static Consumer<Component> deathMessageSetter(PlayerDeathEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::deathMessage;
    }
    return component -> {
      if (component == null) {
        event.setDeathMessage(null);
        return;
      }
      event.setDeathMessage(LegacyComponentSerializer.legacySection().serialize(component));
    };
  }

  public static Supplier<Component> advancementMessage(PlayerAdvancementDoneEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::message;
    }
    return Component::empty;
  }

  public static Consumer<Component> advancementMessageSetter(PlayerAdvancementDoneEvent event) {
    if (Environment.ENVIRONMENT.isPaper()) {
      return event::message;
    }
    return component -> {};
  }

  public static Supplier<Component> playerDisplayName(Player player) {
    if (Environment.ENVIRONMENT.isPaper()) return player::displayName;

    return () -> LegacyComponentSerializer.legacySection().deserialize(player.getDisplayName());
  }

  public static Component displayNameOfItem(ItemStack itemStack) {
    var component = itemStack.displayName().asComponent().hoverEvent(itemStack.asHoverEvent());

    return component;
  }
}
