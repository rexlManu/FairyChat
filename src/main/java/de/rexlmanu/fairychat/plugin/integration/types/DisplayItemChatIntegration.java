package de.rexlmanu.fairychat.plugin.integration.types;

import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import de.rexlmanu.fairychat.plugin.paper.Environment;
import de.rexlmanu.fairychat.plugin.paper.event.EventMessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public class DisplayItemChatIntegration implements Integration, PlaceholderSupport {

  @Override
  public boolean available() {
    return true;
  }

  @Override
  public TagResolver resolveChatMessagePlaceholder(Player player, String message) {
    if (!player.hasPermission("fairychat.feature.displayitem")) return TagResolver.empty();

    var item = player.getInventory().getItemInMainHand();

    if (item.getType().isAir()) return TagResolver.empty();

    Component component;
    if (Environment.PAPER.isPaper()) {
      component = EventMessageUtils.displayNameOfItem(item);
    } else {
      String displayName = item.getItemMeta().getDisplayName();
      if (displayName == null) {
        component = Component.empty();
      } else component = LegacyComponentSerializer.legacySection().deserialize(displayName);
    }

    return TagResolver.resolver("item", Tag.selfClosingInserting(component));
  }
}
