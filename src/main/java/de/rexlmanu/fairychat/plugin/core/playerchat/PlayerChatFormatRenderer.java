package de.rexlmanu.fairychat.plugin.core.playerchat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.rexlmanu.fairychat.plugin.configuration.PluginConfiguration;
import de.rexlmanu.fairychat.plugin.core.mentions.MentionService;
import de.rexlmanu.fairychat.plugin.integration.IntegrationRegistry;
import de.rexlmanu.fairychat.plugin.permission.PermissionProvider;
import de.rexlmanu.fairychat.plugin.utility.LegacySupport;
import io.papermc.paper.chat.ChatRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerChatFormatRenderer implements ChatRenderer {
  private final Provider<PluginConfiguration> configurationProvider;
  private final MiniMessage miniMessage;
  private final PermissionProvider permissionProvider;
  private final MentionService mentionService;
  private final IntegrationRegistry registry;

  @Named("colorMiniMessage")
  private final MiniMessage colorMiniMessage;

  @Override
  public @NotNull Component render(
      @NotNull Player source,
      @NotNull Component sourceDisplayName,
      @NotNull Component message,
      @NotNull Audience viewer) {

    Component formattedMessage = formatMessage(source, message);
    if (!(viewer instanceof Player player)) {
      return formattedMessage;
    }
    return this.mentionService.checkMentions(player, formattedMessage);
  }

  @NotNull
  public Component formatMessage(@NotNull Player source, @NotNull Component message) {
    String group = this.permissionProvider.getGroup(source.getUniqueId());
    String chatFormat = configurationProvider.get().chatFormat();

    if (group != null) {
      chatFormat = configurationProvider.get().groupFormats().getOrDefault(group, chatFormat);
    }

    // Check if the player has the permission to use mini message
    if (source.hasPermission("fairychat.feature.minimessage")) {
      Function<Component, String> serializer = PlainTextComponentSerializer.plainText()::serialize;
      if (configurationProvider.get().legacyColorSupport()) {
        serializer = LegacyComponentSerializer.legacyAmpersand()::serialize;
      }
      String textMessage = serializer.apply(message);
      String miniMessageFormatted = LegacySupport.replaceLegacyWithTags(textMessage);
      message = this.colorMiniMessage.deserialize(miniMessageFormatted);
    }

    @NotNull Component finalMessage = message;

    List<TagResolver> tagResolvers =
        new ArrayList<>(
            this.registry.getPlaceholderSupports().stream()
                .map(
                    chatPlaceholder ->
                        chatPlaceholder.resolvePlayerPlaceholderWithChatMessage(
                            source, finalMessage))
                .toList());

    tagResolvers.add(Placeholder.component("message", message));
    tagResolvers.add(Placeholder.unparsed("server_name", configurationProvider.get().serverName()));

    return this.miniMessage.deserialize(chatFormat, TagResolver.resolver(tagResolvers));
  }
}
