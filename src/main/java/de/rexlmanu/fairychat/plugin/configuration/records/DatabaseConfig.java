package de.rexlmanu.fairychat.plugin.configuration.records;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class DatabaseConfig {
  @Comment("If mysql should be enabled otherwise sqlite will be used")
  private boolean mysql = false;

  @Comment("The url to the database, only required if mysql is enabled")
  private String url = "jdbc:mysql://localhost:3306/fairychat?autoReconnect=true&useSSL=false";

  @Comment("The username to the database, only required if mysql is enabled")
  private String username = "root";

  @Comment("The password to the database, only required if mysql is enabled")
  private String password = "";

  private String playerIgnoreTable = "player_ignores";
}
