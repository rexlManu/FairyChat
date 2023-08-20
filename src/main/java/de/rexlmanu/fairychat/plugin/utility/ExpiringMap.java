package de.rexlmanu.fairychat.plugin.utility;

import java.util.Optional;
import java.util.concurrent.*;

public class ExpiringMap<K, V> {
  private final ConcurrentHashMap<K, ValueWithExpirationTime<V>> map;
  private final ScheduledExecutorService executorService;

  public ExpiringMap() {
    this.map = new ConcurrentHashMap<>();
    this.executorService = Executors.newScheduledThreadPool(1);
  }

  public void put(K key, V value, long duration, TimeUnit unit) {
    ValueWithExpirationTime<V> newValue =
        new ValueWithExpirationTime<>(value, System.nanoTime() + unit.toNanos(duration));
    ValueWithExpirationTime<V> oldValue = this.map.put(key, newValue);

    // If the key already exists, cancel its old expiration task
    if (oldValue != null) {
      oldValue.getRemovalTask().cancel(false);
    }

    // Schedule a new removal task
    ScheduledFuture<?> future =
        this.executorService.schedule(() -> this.map.remove(key, newValue), duration, unit);
    newValue.setRemovalTask(future);
  }

  public V get(K key) {
    ValueWithExpirationTime<V> value = this.map.get(key);
    return value != null && !value.isExpired() ? value.getValue() : null;
  }

  public Optional<V> getOptional(K key) {
    return Optional.ofNullable(this.get(key));
  }

  public long getExpirationTime(K key) {
    ValueWithExpirationTime<V> value = this.map.get(key);
    return value != null ? value.expirationTime : 0;
  }

  public boolean containsKey(K key) {
    return this.map.containsKey(key);
  }

  public V computeIfAbsent(K key, long duration, TimeUnit unit, Callable<V> callable) {
    ValueWithExpirationTime<V> value = this.map.get(key);
    if (value != null && !value.isExpired()) {
      return value.getValue();
    }
    try {
      V newValue = callable.call();
      this.put(key, newValue, duration, unit);
      return newValue;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void remove(K key) {
    ValueWithExpirationTime<V> oldValue = this.map.remove(key);
    if (oldValue != null) {
      oldValue.getRemovalTask().cancel(false);
    }
  }

  private static class ValueWithExpirationTime<V> {
    private final V value;
    private final long expirationTime;
    private volatile ScheduledFuture<?> removalTask;

    public ValueWithExpirationTime(V value, long expirationTime) {
      this.value = value;
      this.expirationTime = expirationTime;
    }

    public V getValue() {
      return this.value;
    }

    public boolean isExpired() {
      return System.nanoTime() > this.expirationTime;
    }

    public void setRemovalTask(ScheduledFuture<?> removalTask) {
      this.removalTask = removalTask;
    }

    public ScheduledFuture<?> getRemovalTask() {
      return this.removalTask;
    }
  }
}
