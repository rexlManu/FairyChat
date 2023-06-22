package de.rexlmanu.fairychat.plugin.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class BroadcastConfig {
  @Comment("The format used for broadcast messages.")
  private String format =
      "<dark_gray>[<gradient:#BE95C4:#9F86C0:#5E548E>Broadcast</gradient>]</dark_gray> <gray><message></gray>";
}
