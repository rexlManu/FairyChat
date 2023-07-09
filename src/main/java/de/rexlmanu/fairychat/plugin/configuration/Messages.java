package de.rexlmanu.fairychat.plugin.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class Messages {
  private String youCantMessageYourself = "<red>Sorry, you can't message yourself.</red>";
  private String youDidntMessageAnyone = "<red>No recent messages received from anyone.</red>";
  private String youCantMessageThisPlayer = "<red>Sorry, you can't message this player.</red>";
  private String youCantIgnoreYourself = "<red>Sorry, you can't ignore yourself.</red>";
  private String youUnignoredUser =
      "<gray>You can receive messages from <#5E548E><name></#5E548E> again.</gray>";
  private String youIgnoredUser =
      "<gray>You will no longer receive messages from <#5E548E><name></#5E548E>.</gray>";
  private String invalidSyntax = "<red>Invalid syntax. Usage: <#5E548E><syntax></#5E548E></red>";

  @Comment("All MiniPlaceholders are supported")
  private String joinMessage =
      "<gray><#5E548E><sender_displayname></#5E548E> joined the server.</gray>";

  @Comment("All MiniPlaceholders are supported")
  private String quitMessage =
      "<gray><#5E548E><sender_displayname></#5E548E> left the server.</gray>";

  private String updateNotification =
      "<newline><gray>A new version of <gradient:#BE95C4:#9F86C0:#5E548E>FairyChat</gradient> is available. You can download it at <#5E548E><click:open_url:'<url>'>here</click></#5E548E>.</gray><newline>";
}
