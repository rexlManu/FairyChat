package de.rexlmanu.fairychat.plugin.core.user;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import java.lang.reflect.Type;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserFactory {
  private static final Type USER_TYPE = new TypeToken<User>() {}.getType();
  private final Gson gson;

  public User createFromPlayer(Player player) {
    return new User(player.getUniqueId(), player.getName(), Constants.SERVER_IDENTITY_ORIGIN);
  }

  public String serialize(User user) {
    return this.gson.toJson(user, USER_TYPE);
  }

  public User deserialize(String json) {
    return this.gson.fromJson(json, USER_TYPE);
  }
}
