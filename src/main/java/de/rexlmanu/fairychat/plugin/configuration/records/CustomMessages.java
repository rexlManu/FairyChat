package de.rexlmanu.fairychat.plugin.configuration.records;

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
  private boolean broadcastJoinMessages = false;

  @Comment({
    "If you want to broadcast the messages to all servers via redis.",
    "Be careful, with quick connect like velocity does, the quit event is triggered after the join event, so the message quit message will be sent after the join message."
  })
  private boolean broadcastQuitMessages = false;

  @Comment(
      "This will be used for the death and advancement message for replacing the normal player name")
  private String customName = "<sender_displayname>";
}
