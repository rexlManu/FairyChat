package de.rexlmanu.fairychat.plugin;

import de.rexlmanu.fairychat.plugin.utility.ServerIdentity;

public class Constants {
  private Constants() {
    throw new UnsupportedOperationException();
  }
  /*
   * The server id is used to identify the server in the redis network.
   */
  public static final ServerIdentity SERVER_IDENTITY_ORIGIN = ServerIdentity.random();

  public static final String MESSAGING_CHANNEL = "fairychat:messaging";
  public static final String BROADCAST_CHANNEL = "fairychat:broadcast";
  public static final String PRIVATE_MESSAGING_CHANNEL = "fairychat:private_messaging";
  public static final String USER_EVENTS_LOGIN_CHANNEL = "fairychat:user-events:login";
  public static final String USER_EVENTS_LOGOUT_CHANNEL = "fairychat:user-events:logout";
  public static final String USER_IGNORE_UPDATE_CHANNEL = "fairychat:user-ignore-update";
  public static final String USERS_KEY = "fairychat:users";
  public static final String USERNAMES_KEY = "fairychat:usernames";
  public static final String LAST_RECIPIENTS_KEY = "fairychat:last_recipients";

  public static final int BSTATS_ID = 18786;

  public static final String VERSION_URL =
      "https://api.github.com/repos/rexlManu/FairyChat/releases/latest";
}
