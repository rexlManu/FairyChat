package de.rexlmanu.fairychat.plugin.redis;

import static de.rexlmanu.fairychat.plugin.Constants.BROADCAST_CHANNEL;
import static de.rexlmanu.fairychat.plugin.Constants.MESSAGING_CHANNEL;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.rexlmanu.fairychat.plugin.Constants;
import de.rexlmanu.fairychat.plugin.FairyChatConfiguration;
import de.rexlmanu.fairychat.plugin.redis.data.BroadcastMessageData;
import de.rexlmanu.fairychat.plugin.redis.data.PlayerChatMessageData;
import de.rexlmanu.fairychat.plugin.utility.annotation.PluginLogger;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

@Singleton
public class RedisConnector {
  private final RedisCredentials credentials;
  private final Logger logger;
  private final Injector injector;
  private final JavaPlugin plugin;
  private final Gson gson;
  private final Server server;
  private final Map<Class<?>, MessageChannelHandler<?>> handlers = new HashMap<>();
  private JedisPool jedisPool;

  @Inject
  public RedisConnector(
      FairyChatConfiguration configuration,
      @PluginLogger Logger logger,
      Injector injector,
      JavaPlugin plugin,
      Gson gson,
      Server server) {
    this.credentials = configuration.redisCredentials();
    this.logger = logger;
    this.injector = injector;
    this.plugin = plugin;
    this.gson = gson;
    this.server = server;
  }

  public void open() {
    if (!this.credentials.enabled()) {
      return;
    }
    this.jedisPool = new JedisPool(this.credentials.url());

    this.logger.info("Using redis for broadcasting messages.");

    this.registerHandler(MESSAGING_CHANNEL, PlayerChatMessageData.class);
    this.registerHandler(BROADCAST_CHANNEL, BroadcastMessageData.class);

    this.listen(
        PlayerChatMessageData.class,
        data -> {
          // If the server id is the same as the current server id, the message was sent by this
          if (data.serverId().equals(Constants.SERVER_ID)) return;
          this.server.getOnlinePlayers().forEach(player -> player.sendMessage(data.message()));
        });

    this.listen(
        BroadcastMessageData.class,
        data ->
            this.server.getOnlinePlayers().forEach(player -> player.sendMessage(data.message())));
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

  public void useResourceAsync(Consumer<Jedis> consumer) {
    this.plugin
        .getServer()
        .getScheduler()
        .runTaskAsynchronously(this.plugin, () -> this.useResource(consumer));
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
