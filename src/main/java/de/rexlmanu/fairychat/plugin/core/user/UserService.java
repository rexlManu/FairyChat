package de.rexlmanu.fairychat.plugin.core.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
  void login(User user);

  void logout(User user);

  List<User> onlineUsers();

  Optional<User> findUserById(UUID id);

  Optional<User> findUserByUsername(String username);
}
