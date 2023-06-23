package de.rexlmanu.fairychat.plugin.configuration;

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
}
