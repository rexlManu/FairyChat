package de.rexlmanu.fairychat.plugin.redis.channel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
public class MessageChannelHandler<D> extends JedisPubSub {
  @Getter private final String channelName;
  private final TypeToken<D> typeToken;
  private final List<Consumer<D>> listeners = new CopyOnWriteArrayList<>();
  private final Gson gson;
  @Inject private Logger logger;

  @Override
  public void onMessage(String channel, String message) {
    if (!channel.equals(channelName)) return;

    try {
      D data = this.gson.fromJson(message, this.typeToken);
      this.listeners.forEach(listener -> listener.accept(data));
    } catch (Exception e) {
      this.logger.warning("Failed to parse message from channel " + channel + ": " + message);
    }
  }

  public void addListener(Consumer<D> listener) {
    this.listeners.add(listener);
  }
}
