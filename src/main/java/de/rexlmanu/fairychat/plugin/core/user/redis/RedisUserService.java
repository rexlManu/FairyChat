package de.rexlmanu.fairychat.plugin.core.user.redis;

import static de.rexlmanu.fairychat.plugin.Constants.USERNAMES_KEY;
import static de.rexlmanu.fairychat.plugin.Constants.USERS_KEY;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.core.user.User;
import de.rexlmanu.fairychat.plugin.core.user.UserFactory;
import de.rexlmanu.fairychat.plugin.core.user.UserService;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisUserService implements UserService {
  private final UserFactory userFactory;
  private final RedisConnector connector;

  @Override
  public void login(User user) {
    this.connector.useResource(
        jedis -> {
          String serializedUser = userFactory.serialize(user);
          jedis.hset(USERS_KEY, user.uniqueId().toString(), serializedUser);
          jedis.hset(USERNAMES_KEY, user.username(), user.uniqueId().toString());
        });
  }

  @Override
  public void logout(User user) {
    this.connector.useResource(
        jedis -> {
          jedis.hdel(USERS_KEY, user.uniqueId().toString());
          jedis.hdel(USERNAMES_KEY, user.username());
        });
  }

  public List<User> onlineUsers() {
    return connector.useQuery(
        jedis -> jedis.hvals(USERS_KEY).stream().map(userFactory::deserialize).toList());
  }

  public Optional<User> findUserById(UUID uniqueId) {
    return connector.useQuery(
        jedis -> {
          String json = jedis.hget(USERS_KEY, uniqueId.toString());
          if (json == null) {
            return Optional.empty();
          }
          return Optional.of(userFactory.deserialize(json));
        });
  }

  public Optional<User> findUserByUsername(String username) {
    return connector.useQuery(
        jedis -> {
          String uniqueId = jedis.hget(USERNAMES_KEY, username);
          if (uniqueId == null) {
            return Optional.empty();
          }
          String json = jedis.hget(USERS_KEY, uniqueId);
          if (json == null) {
            return Optional.empty();
          }
          return Optional.of(userFactory.deserialize(json));
        });
  }

  public long onlineUsersCount() {
    return connector.useQuery(jedis -> jedis.hlen(USERS_KEY));
  }
}
