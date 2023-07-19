package de.rexlmanu.fairychat.plugin.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.rexlmanu.fairychat.plugin.configuration.records.BroadcastConfig;
import de.rexlmanu.fairychat.plugin.configuration.records.CustomMessages;
import de.rexlmanu.fairychat.plugin.configuration.records.DatabaseConfig;
import de.rexlmanu.fairychat.plugin.configuration.records.MentionConfig;
import de.rexlmanu.fairychat.plugin.configuration.records.Messages;
import de.rexlmanu.fairychat.plugin.configuration.records.PrivateMessagingConfig;
import de.rexlmanu.fairychat.plugin.configuration.records.RedisCredentials;
import java.util.Map;
import lombok.Getter;

@Configuration
@Getter
public class PluginConfiguration {
  @Comment("Should the plugin check for updates?")
  private boolean checkForUpdates = true;

  @Comment("The server name that should be displayed for the <server_name> placeholder.")
  private String serverName = "";

  @Comment("Redis credentials used for communicating between other servers.")
  private RedisCredentials redisCredentials = new RedisCredentials(false, "redis://localhost:6379");

  @Comment("Configuration for database")
  private DatabaseConfig database = new DatabaseConfig();

  @Comment({
    "The format of chat messages in minimessage format.",
    "You can use any placeholder from the following list:",
    "https://github.com/MiniPlaceholders/MiniPlaceholders/wiki/Placeholders",
    "If you wise to use PlaceholderAPI placeholders, you need the following tag",
    "<papi:(placeholder name)> e.g. <papi:player_displayname>",
    "Built-in placeholders:",
    "<sender_name> - The name of the player who sent the message",
    "<sender_displayname> - The display name of the player who sent the message",
    "<message> - The message that was sent",
    "<server_name> - The server name that was configured in the config.yml",
  })
  private String chatFormat =
      "<#5E548E><sender_displayname></#5E548E> <dark_gray>»</dark_gray> <gray><message></gray>";

  @Comment({
    "Define formats based on the player's group.",
    "Supported permission systems: LuckPerms",
    "You can use any placeholder from PlaceholderAPI or MiniPlaceholder",
    "To use placeholders from PlaceholderAPI, use the following format:",
    "<papi:(placeholder name)> e.g. <papi:player_displayname>"
  })
  private Map<String, String> groupFormats =
      Map.of(
          "admin",
          "<dark_red><sender_displayname></dark_red> <dark_gray>»</dark_gray> <white><message></white>");

  @Comment("Should the player chat messages be displayed in the console when sent via Redis?")
  private boolean displayChatInConsole = true;

  @Comment("Configuration for private messages.")
  private PrivateMessagingConfig privateMessaging = new PrivateMessagingConfig();

  @Comment("Configuration for broadcast messages.")
  private BroadcastConfig broadcast = new BroadcastConfig();

  @Comment({
    "Configure the plugin's messages.",
    "https://github.com/MiniPlaceholders/MiniPlaceholders/wiki/Placeholders",
    "If you wise to use PlaceholderAPI placeholders, you need the following tag",
    "<papi:(placeholder name)> e.g. <papi:player_displayname>"
  })
  private Messages messages = new Messages();

  @Comment(
      "Enable this if you have other plugins that messes with the chat and introduces legacy chat colors")
  private boolean legacyColorSupport = false;

  @Comment("Configuration for custom messages.")
  private CustomMessages customMessages = new CustomMessages();

  @Comment("Configuration for mentions.")
  private MentionConfig mention =
      new MentionConfig(
          "<#5E548E>@</#5E548E><#BE95C4><highlight_name></#BE95C4>",
          "<sender_name>",
          "<sender_name>",
          "block.note_block.pling",
          1F,
          1.30F);

  @Comment("The cooldown, in seconds, that a player has to wait before chatting again.")
  private int chattingCooldown = 0;

  @Comment("The threshold, how many messages a player can send before the cooldown is applied.")
  private int chattingThreshold = 0;
}
