package de.rexlmanu.fairychat.plugin.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class CustomMessages {
  @Comment("If you want to broadcast the messages to all servers via redis.")
  private boolean broadcastDeathMessages = true;

  @Comment("If you want to broadcast the messages to all servers via redis.")
  private boolean broadcastAdvancementMessages = true;

  @Comment("If you want to broadcast the messages to all servers via redis.")
  private boolean broadcastJoinMessages = true;

  @Comment("If you want to broadcast the messages to all servers via redis.")
  private boolean broadcastQuitMessages = true;
}
