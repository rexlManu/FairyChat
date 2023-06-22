package de.rexlmanu.fairychat.plugin.configuration;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

@Configuration
@Getter
public class PrivateMessagingConfig {

  @Comment("The format used for private messaging.")
  private String format =
      "<dark_gray>[<#5E548E>PM</#5E548E>] <gray><sender_name></gray> → <#9F86C0><recipient_name></#9F86C0> »<dark_gray> <gray><message></gray>";

  @Comment("The duration (in seconds) until a recipient can no longer respond.")
  private long recipientExpirationSeconds = TimeUnit.SECONDS.toSeconds(90);

  @Comment("Aliases for commands.")
  private Map<String, String[]> aliases =
      Map.of(
          "pm", new String[] {"msg", "message", "tell", "whisper"},
          "reply", new String[] {"r", "answer"});
}
