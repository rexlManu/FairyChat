package de.rexlmanu.fairychat.plugin;

import java.util.UUID;

public class Constants {
  private Constants() {
    throw new UnsupportedOperationException();
  }
  /*
   * The server id is used to identify the server in the redis network.
   */
  public static final UUID SERVER_ID = UUID.randomUUID();

  public static final String MESSAGING_CHANNEL = "fairychat:messaging";
  public static final String BROADCAST_CHANNEL = "fairychat:broadcast";

  public static final int BSTATS_ID = 18786;
}
