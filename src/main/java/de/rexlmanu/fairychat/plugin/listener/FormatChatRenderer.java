package de.rexlmanu.fairychat.plugin.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.rexlmanu.fairychat.plugin.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.permission.PermissionProvider;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.papermc.paper.chat.ChatRenderer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FormatChatRenderer implements ChatRenderer {
  private final FairyChatConfiguration configuration;
  private final MiniMessage miniMessage;
  private final PermissionProvider permissionProvider;

  @Named("colorMiniMessage")
  private final MiniMessage colorMiniMessage;

  @Override
  public @NotNull Component render(
      @NotNull Player source,
      @NotNull Component sourceDisplayName,
      @NotNull Component message,
      @NotNull Audience viewer) {

    return formatMessage(source, message);
  }

  @NotNull
  public Component formatMessage(@NotNull Player source, @NotNull Component message) {
    String group = this.permissionProvider.getGroup(source.getUniqueId());
    String chatFormat = this.configuration.chatFormat();

    if (group != null) {
      chatFormat = this.configuration.groupFormats().getOrDefault(group, chatFormat);
    }

    // Check if the player has the permission to use mini message
    if (source.hasPermission("fairychat.feature.minimessage")) {
      message =
          this.colorMiniMessage.deserialize(
              PlainTextComponentSerializer.plainText().serialize(message));
    }

    return this.miniMessage.deserialize(
        chatFormat,
        MiniPlaceholders.getAudiencePlaceholders(source),
        Placeholder.component("message", message));
  }
}
