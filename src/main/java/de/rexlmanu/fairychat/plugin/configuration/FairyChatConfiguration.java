package de.rexlmanu.fairychat.plugin.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.Map;
import lombok.Getter;

@Configuration
@Getter
public class FairyChatConfiguration {
  @Comment("Should the plugin check for updates?")
  private boolean checkForUpdates = true;

  @Comment("Redis credentials used for communicating between other servers.")
  private RedisCredentials redisCredentials = new RedisCredentials(false, "redis://localhost:6379");

  @Comment({
    "The format of chat messages in minimessage format.",
    "You can use any placeholder from the following list:",
    "https://github.com/MiniPlaceholders/MiniPlaceholders/wiki/Placeholders"
  })
  private String chatFormat =
      "<#5E548E><player_name></#5E548E> <dark_gray>»</dark_gray> <gray><message></gray>";

  @Comment({
    "Define formats based on the player's group.",
    "Supported permission systems: LuckPerms"
  })
  private Map<String, String> groupFormats =
      Map.of(
          "admin",
          "<dark_red><player_name></dark_red> <dark_gray>»</dark_gray> <white><message></white>");

  @Comment("Should the player chat messages be displayed in the console when sent via Redis?")
  private boolean displayChatInConsole = true;

  @Comment("Configuration for private messages.")
  private PrivateMessagingConfig privateMessaging = new PrivateMessagingConfig();

  @Comment("Configuration for broadcast messages.")
  private BroadcastConfig broadcast = new BroadcastConfig();

  @Comment("Configure the plugin's messages.")
  private Messages messages = new Messages();
}
