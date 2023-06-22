package de.rexlmanu.fairychat.plugin.core.user.defaults;

import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** This is the default implementation of the {@link UserService} interface. */
@Singleton
public class DefaultUserService implements UserService {
  private final Map<UUID, User> users = new ConcurrentHashMap<>();

  @Override
  public void login(User user) {
    this.users.put(user.uniqueId(), user);
  }

  @Override
  public void logout(User user) {
    this.users.remove(user.uniqueId());
  }

  @Override
  public List<User> onlineUsers() {
    return List.copyOf(this.users.values());
  }

  @Override
  public Optional<User> findUserById(UUID id) {
    return Optional.ofNullable(this.users.get(id));
  }

  @Override
  public Optional<User> findUserByUsername(String username) {
    return this.onlineUsers().stream()
        .filter(user -> user.username().equalsIgnoreCase(username))
        .findFirst();
  }
}
