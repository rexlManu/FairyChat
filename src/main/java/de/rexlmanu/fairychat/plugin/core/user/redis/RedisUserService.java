package de.rexlmanu.fairychat.plugin.core.user.redis;

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
  private static final String REDIS_KEY = "fairychat:users";
  private static final String USERNAME_KEY = "fairychat:usernames";
  private final UserFactory userFactory;
  private final RedisConnector connector;

  @Override
  public void login(User user) {
    connector.useResourceAsync(
        jedis -> {
          String serializedUser = userFactory.serialize(user);
          jedis.hset(REDIS_KEY, user.uniqueId().toString(), serializedUser);
          jedis.hset(USERNAME_KEY, user.username(), user.uniqueId().toString());
        });
  }

  @Override
  public void logout(User user) {
    connector.useResourceAsync(
        jedis -> {
          jedis.hdel(REDIS_KEY, user.uniqueId().toString());
          jedis.hdel(USERNAME_KEY, user.username());
        });
  }

  public List<User> onlineUsers() {
    return connector.useQuery(
        jedis -> jedis.hvals(REDIS_KEY).stream().map(userFactory::deserialize).toList());
  }

  public Optional<User> findUserById(UUID uniqueId) {
    return connector.useQuery(
        jedis -> {
          String json = jedis.hget(REDIS_KEY, uniqueId.toString());
          if (json == null) {
            return Optional.empty();
          }
          return Optional.of(userFactory.deserialize(json));
        });
  }

  public Optional<User> findUserByUsername(String username) {
    return connector.useQuery(
        jedis -> {
          String uniqueId = jedis.hget(USERNAME_KEY, username);
          if (uniqueId == null) {
            return Optional.empty();
          }
          String json = jedis.hget(REDIS_KEY, uniqueId);
          if (json == null) {
            return Optional.empty();
          }
          return Optional.of(userFactory.deserialize(json));
        });
  }
}
