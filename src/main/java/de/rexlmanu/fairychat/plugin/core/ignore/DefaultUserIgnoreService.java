package de.rexlmanu.fairychat.plugin.core.ignore;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.core.ignore.redis.UserIgnoreDto;
import de.rexlmanu.fairychat.plugin.database.DatabaseClient;
import de.rexlmanu.fairychat.plugin.redis.RedisConnector;
import de.rexlmanu.fairychat.plugin.utility.ExpiringMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class DefaultUserIgnoreService implements UserIgnoreService {
  private final DatabaseClient client;
  private final ExpiringMap<String, Boolean> lookupCache = new ExpiringMap<>();
  private final RedisConnector connector;

  @Override
  public boolean isIgnored(UUID userId, UUID targetId) {
    return this.lookupCache.computeIfAbsent(
        this.createKey(userId, targetId),
        5,
        TimeUnit.MINUTES,
        () ->
            this.client
                .newBuilder(this.client.queries().selectUserIgnore())
                .appends(userId.toString(), targetId.toString())
                .query(resultSet -> true)
                .orElse(false));
  }

  @Override
  public void setIgnored(UUID userId, UUID targetId, boolean ignored) {
    if (ignored) {
      this.client
          .newBuilder(this.client.queries().insertUserIgnore())
          .appends(userId.toString(), targetId.toString())
          .execute();
    } else {
      this.client
          .newBuilder(this.client.queries().deleteUserIgnore())
          .appends(userId.toString(), targetId.toString())
          .execute();
    }

    String cacheKey = this.createKey(userId, targetId);
    this.invalidate(cacheKey);

    if (!this.connector.available()) return;

    this.connector.publish(Constants.USER_IGNORE_UPDATE_CHANNEL, new UserIgnoreDto(cacheKey));
  }

  private String createKey(UUID userId, UUID targetId) {
    return userId.toString() + "-" + targetId.toString();
  }

  public void invalidate(String key) {
    this.lookupCache.remove(key);
  }
}
