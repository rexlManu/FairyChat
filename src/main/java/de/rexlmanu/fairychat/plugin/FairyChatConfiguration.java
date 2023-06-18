package de.rexlmanu.fairychat.plugin;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.rexlmanu.fairychat.plugin.redis.RedisCredentials;
import java.util.Map;
import lombok.Getter;

@Configuration
@Getter
public class FairyChatConfiguration {
  @Comment("Redis credentials, used to broadcast messages to other servers.")
  private RedisCredentials redisCredentials = new RedisCredentials(false, "redis://localhost:6379");

  @Comment({
    "The format of chat messages as minimessage.",
    "You can use any placeholder from",
    "https://github.com/MiniPlaceholders/MiniPlaceholders/wiki/Placeholders"
  })
  private String chatFormat = "<player_name> <dark_gray>» <gray><message>";

  @Comment({
    "Define formats based on the player's group.",
    "Following permission systems are supported:",
    "LuckPerms"
  })
  private Map<String, String> groupFormats =
      Map.of("admin", "<dark_red><player_name> <dark_gray>» <white><message>");
}
