package de.rexlmanu.fairychat.plugin.integration.types;

import org.bukkit.entity.Player;

import de.rexlmanu.fairychat.plugin.integration.Integration;
import de.rexlmanu.fairychat.plugin.integration.chat.PlaceholderSupport;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class DisplayItemChatIntegration implements Integration, PlaceholderSupport {

    @Override
    public boolean available() {
        return true;
    }

    @Override
    public TagResolver resolveChatMessagePlaceholder(Player player, String message) {
        if (!player.hasPermission("fairychat.feature.displayitem"))
            return TagResolver.empty();

        var item = player.getInventory().getItemInMainHand();

        if (item.getType().isAir())
            return TagResolver.empty();

        var component = item.displayName().asComponent().hoverEvent(item.asHoverEvent());

        return TagResolver.resolver("item", Tag.selfClosingInserting(component));
    }

}
