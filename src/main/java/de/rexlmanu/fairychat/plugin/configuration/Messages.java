package de.rexlmanu.fairychat.plugin.configuration;

import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class Messages {
  private String youCantMessageYourself = "<red>Sorry, you can't message yourself.</red>";
  private String youDidntMessageAnyone = "<red>No recent messages received from anyone.</red>";
}
