package de.rexlmanu.fairychat.plugin.redis.channel;

import de.rexlmanu.fairychat.plugin.redis.RedisConnector;

public interface RedisChannelSubscriber<T> {
  Class<T> getDataType();

  void handle(T data);

  default void register(RedisConnector connector) {
    connector.listen(this.getDataType(), this::handle);
  }
}
