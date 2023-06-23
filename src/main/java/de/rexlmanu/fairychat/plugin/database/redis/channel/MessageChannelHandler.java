package de.rexlmanu.fairychat.plugin.database.redis.channel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPubSub;

@RequiredArgsConstructor
public class MessageChannelHandler<D> extends JedisPubSub {
  @Getter private final String channelName;
  private final TypeToken<D> typeToken;
  private final List<Consumer<D>> listeners = new ArrayList<>();
  private final Gson gson;

  @Override
  public void onMessage(String channel, String message) {
    if (!channel.equals(channelName)) return;

    try {
      D data = this.gson.fromJson(message, this.typeToken);
      this.listeners.forEach(listener -> listener.accept(data));
    } catch (Exception e) {
      throw new RuntimeException("Failed to deserialize message.", e);
    }
  }

  public void addListener(Consumer<D> listener) {
    this.listeners.add(listener);
  }
}
