package de.rexlmanu.fairychat.plugin.redis;

import static de.rexlmanu.fairychat.plugin.Constants.BROADCAST_CHANNEL;
import static de.rexlmanu.fairychat.plugin.Constants.MESSAGING_CHANNEL;
import static de.rexlmanu.fairychat.plugin.Constants.PRIVATE_MESSAGING_CHANNEL;
import static de.rexlmanu.fairychat.plugin.Constants.USER_IGNORE_UPDATE_CHANNEL;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.configuration.RedisCredentials;
import de.rexlmanu.fairychat.plugin.core.broadcast.BroadcastMessageData;
import de.rexlmanu.fairychat.plugin.core.ignore.redis.UserIgnoreDto;
import de.rexlmanu.fairychat.plugin.core.playerchat.PlayerChatMessageData;
import de.rexlmanu.fairychat.plugin.core.privatemessaging.redis.PrivateMessageData;
import de.rexlmanu.fairychat.plugin.database.Connector;
import de.rexlmanu.fairychat.plugin.redis.channel.MessageChannelHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class RedisConnector implements Connector {
  private final RedisCredentials credentials;
  private final Logger logger;
  private final Injector injector;
  private final JavaPlugin plugin;
  private final Gson gson;
  private final Map<Class<?>, MessageChannelHandler<?>> handlers = new HashMap<>();
  private JedisPool jedisPool;

  public void open() {
    if (!this.credentials.enabled()) {
      return;
    }
    this.jedisPool = new JedisPool(this.credentials.url());

    this.logger.info("Using redis for broadcasting messages.");

    this.registerHandler(MESSAGING_CHANNEL, PlayerChatMessageData.class);
    this.registerHandler(BROADCAST_CHANNEL, BroadcastMessageData.class);
    this.registerHandler(PRIVATE_MESSAGING_CHANNEL, PrivateMessageData.class);
    this.registerHandler(USER_IGNORE_UPDATE_CHANNEL, UserIgnoreDto.class);
  }

  public void close() {
    if (this.jedisPool == null) {
      return;
    }
    this.handlers.values().forEach(JedisPubSub::unsubscribe);
    this.jedisPool.close();
  }

  public boolean available() {
    return this.jedisPool != null;
  }

  private <D> void registerHandler(String channelName, Class<D> dataClass) {
    MessageChannelHandler<D> handler =
        new MessageChannelHandler<>(channelName, TypeToken.get(dataClass), this.gson);
    this.injector.injectMembers(handler);
    this.handlers.put(dataClass, handler);

    this.useResourceAsync(jedis -> jedis.subscribe(handler, handler.channelName()));
  }

  public void useResource(Consumer<Jedis> consumer) {
    // This case should not happen, if the redis is not available, resources should not be used.
    if (!this.available()) {
      throw new RuntimeException("Redis is not available.");
    }
    try (Jedis jedis = this.jedisPool.getResource()) {
      consumer.accept(jedis);
    }
  }

  public <T> T useQuery(Function<Jedis, T> queryFunction) {
    // This case should not happen, if the redis is not available, resources should not be used.
    if (!this.available()) {
      throw new RuntimeException("Redis is not available.");
    }
    try (Jedis jedis = this.jedisPool.getResource()) {
      return queryFunction.apply(jedis);
    }
  }

  public void useResourceAsync(Consumer<Jedis> consumer) {
    this.plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(this.plugin, () -> this.useResource(consumer));
  }

  public <T> CompletableFuture<T> useQueryAsync(Function<Jedis, T> function) {
    return CompletableFuture.supplyAsync(() -> this.useQuery(function));
  }

  public <D> void listen(Class<D> dataClass, Consumer<D> listener) {
    MessageChannelHandler<D> handler = (MessageChannelHandler<D>) this.handlers.get(dataClass);
    if (handler == null) {
      throw new RuntimeException("No handler found for class " + dataClass.getName());
    }
    handler.addListener(listener);
  }

  public <D> void publish(String channelName, D data) {
    this.useResourceAsync(
        jedis ->
            jedis.publish(
                channelName, this.gson.toJson(data, TypeToken.get(data.getClass()).getType())));
  }
}
