/*
 * Copyright (C) 2018 justlive1
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.common.base.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 有失效时间的 Map，每个
 *
 * @author wubo
 */
public class ExpiringMap<K, V> implements ConcurrentMap<K, V>, Serializable {

  private static final long serialVersionUID = 1L;

  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock readLock = readWriteLock.readLock();
  private final Lock writeLock = readWriteLock.writeLock();
  private ScheduledExecutorService executorService;

  /**
   * 失效清除策略
   */
  public enum CleanPolicy {
    /**
     * 定时任务
     */
    SCHEDULE,
    /**
     * 累计
     */
    ACCUMULATE;
  }

  /**
   * 失效监听
   */
  private List<ExpiredListener<K, V>> asyncExpiredListeners;

  /**
   * 最大数量
   */
  private int maxSize;

  /**
   * 有效期
   */
  private long duration;

  /**
   * 有效期单位
   */
  private TimeUnit timeUnit;

  /**
   * 失效清除策略
   */
  private CleanPolicy cleanPolicy;

  /**
   * 实际数据
   */
  private TreeMap<K, ExpiringValue<V>> data;

  /**
   * 构造函数
   *
   * @param builder 构造器
   */
  private ExpiringMap(final Builder<K, V> builder) {
    asyncExpiredListeners = builder.asyncExpiredListeners;
    maxSize = builder.maxSize;
    duration = builder.duration;
    timeUnit = builder.timeUnit;
    cleanPolicy = builder.cleanPolicy;
    runCleanPolicy();
    data = new TreeMap<>();
  }

  /**
   * 获取构造器
   *
   * @return 构造器
   */
  public static <K, V> Builder<K, V> builder() {
    return new Builder<>();
  }

  /**
   * 获取默认ExpiringMap，最大值Integer.MAX_VALUE
   *
   * @return
   */
  public static <K, V> ExpiringMap<K, V> create() {
    return ExpiringMap.<K, V>builder().build();
  }

  @Override
  public int size() {
    try {
      readLock.lock();
      return (int) data.values().parallelStream().filter(r -> !r.isExpired()).count();
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean isEmpty() {
    return this.size() > 0;
  }

  @Override
  public boolean containsKey(Object key) {
    try {
      readLock.lock();
      ExpiringValue<V> value = data.get(key);
      return value != null && !value.isExpired();
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public boolean containsValue(Object value) {
    // TODO
    return false;
  }

  @Override
  public V get(Object key) {
    try {
      readLock.lock();
      ExpiringValue<V> value = data.get(key);
      if (value == null || value.isExpired()) {
        return null;
      }
      return value.value;
    } finally {
      readLock.unlock();
      accessRecord();
    }
  }

  @Override
  public V put(K key, V value) {
    return put(key, value, ExpiringValue.NOT_EXPIRED);
  }

  /**
   * 添加默认时间单位的会失效的键值
   *
   * @param key 键
   * @param value 值
   * @param duration 期限
   * @return 已存在的值
   */
  public V put(K key, V value, long duration) {
    return put(key, value, duration, this.timeUnit);
  }

  /**
   * 添加会失效的键值
   *
   * @param key 键
   * @param value 值
   * @param duration 期限
   * @param timeUnit 时间单位
   * @return 已存在的值
   */
  public V put(K key, V value, long duration, TimeUnit timeUnit) {
    try {
      writeLock.lock();
      ExpiringValue<V> wrapValue = new ExpiringValue<>(value, duration, timeUnit);
      ExpiringValue<V> preValue = data.put(key, wrapValue);
      if (preValue != null && !preValue.isExpired()) {
        return preValue.value;
      }
      return null;
    } finally {
      writeLock.unlock();
      createRecord();
    }
  }

  @Override
  public V remove(Object key) {
    try {
      writeLock.lock();
      ExpiringValue<V> value = data.remove(key);
      if (value == null || value.isExpired()) {
        return null;
      }
      return value.value;
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    try {
      writeLock.lock();
      Map<K, ExpiringValue<V>> map = new HashMap<>(m.size());
      for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
        map.put(entry.getKey(), new ExpiringValue<>(entry.getValue()));
      }
      data.putAll(map);
    } finally {
      writeLock.unlock();
      createRecord();
    }
  }

  @Override
  public void clear() {
    try {
      writeLock.lock();
      data.clear();
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public Set<K> keySet() {
    try {
      readLock.lock();
      return data.entrySet().parallelStream().filter(r -> !r.getValue().isExpired())
          .map(r -> r.getKey()).collect(
              Collectors.toSet());
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public Collection<V> values() {
    try {
      readLock.lock();
      return data.values().parallelStream().filter(r -> !r.isExpired()).map(r -> r.value).collect(
          Collectors.toList());
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public Set<Map.Entry<K, V>> entrySet() {
    try {
      readLock.lock();
      return data.entrySet().parallelStream().filter(r -> !r.getValue().isExpired())
          .map(r -> new Entry<>(r.getKey(), r.getValue().value)).collect(Collectors.toSet());
    } finally {
      readLock.unlock();
    }
  }

  @Override
  public V putIfAbsent(K key, V value) {
    ExpiringValue<V> wrapValue = new ExpiringValue<>(value);
    ExpiringValue<V> preVal = data.putIfAbsent(key, wrapValue);
    if (preVal == null || preVal.expireAt < System.currentTimeMillis()) {
      return null;
    }
    return preVal.value;
  }

  @Override
  public boolean remove(Object key, Object value) {
    // TODO
    return false;
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    // TODO
    return false;
  }

  @Override
  public V replace(K key, V value) {
    // TODO
    return null;
  }

  private void createRecord() {
    // TODO
  }

  private void accessRecord() {
    // TODO
  }

  private void runCleanPolicy() {

  }

  private void expiredClean() {
    try {
      writeLock.lock();
      for (Map.Entry<K, ExpiringValue<V>> entry : data.entrySet()) {
        if (entry.getValue().isExpired()) {
          K key = entry.getKey();
          V value = entry.getValue().value;
          data.remove(key);
          notifyListener(key, value);
        }
      }
    } finally {
      writeLock.unlock();
    }

  }

  private void notifyListener(K key, V value) {
    if (asyncExpiredListeners != null && !asyncExpiredListeners.isEmpty()) {
      for (ExpiredListener listener : asyncExpiredListeners) {
        listener.expire(key, value);
      }
    }
  }

  private void runScheduleCleanPolicy() {
    executorService =
        new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
            .namingPattern("ExpiringMap-Clean-Pool-%d").daemon(true).build());
    executorService.scheduleWithFixedDelay(() -> expiredClean(),  60, 60, TimeUnit.SECONDS);
  }

  /**
   * 失效监听
   *
   * @param <K> 泛型
   * @param <V> 泛型
   * @author wubo
   */
  public interface ExpiredListener<K, V> {

    /**
     * 失效处理
     *
     * @param key 建
     * @param value 值
     */
    void expire(K key, V value);
  }

  /**
   * 构建器
   *
   * @param <K> 泛型
   * @param <V> 泛型
   * @author wubo
   */
  public static final class Builder<K, V> {

    private List<ExpiredListener<K, V>> asyncExpiredListeners;
    private int maxSize = Integer.MAX_VALUE;
    private long duration = -1;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private CleanPolicy cleanPolicy = CleanPolicy.ACCUMULATE;

    private Builder() {
    }

    /**
     * 设置最大数量
     *
     * @param maxSize 最大数量
     * @return 构造器
     */
    public Builder<K, V> maxSize(int maxSize) {
      if (maxSize <= 0) {
        throw new IllegalArgumentException("maxSize should be positive");
      }
      this.maxSize = maxSize;
      return this;
    }

    /**
     * 增加单个异步失效监听
     *
     * @param listener 监听
     * @return 构造器
     */
    public Builder<K, V> asyncExpiredListeners(ExpiredListener<K, V> listener) {
      Checks.notNull(listener, "listener can not be null");
      if (asyncExpiredListeners == null) {
        asyncExpiredListeners = new ArrayList<>();
      }
      asyncExpiredListeners.add(listener);
      return this;
    }

    /**
     * 增加异步失效监听列表
     *
     * @param listeners 监听
     * @return 构造器
     */
    public Builder<K, V> asyncExpiredListeners(List<ExpiredListener<K, V>> listeners) {
      Checks.notNull(listeners, "listeners can not be null");
      asyncExpiredListeners = listeners;
      return this;
    }

    /**
     * 默认有效期
     *
     * @param duration 有效期限
     * @param timeUnit 时间单位
     * @return 构造器
     */
    public Builder<K, V> expiration(long duration, TimeUnit timeUnit) {
      if (duration <= 0) {
        throw new IllegalArgumentException("duration should be positive");
      }
      Checks.notNull(timeUnit, "timeUnit can not be null");
      this.duration = duration;
      this.timeUnit = timeUnit;
      return this;
    }

    /**
     * 构造ExpiringMap
     *
     * @return ExpiringMap
     */
    public ExpiringMap<K, V> build() {
      return new ExpiringMap<>(this);
    }
  }

  /**
   * 带失效时间的包装
   *
   * @param <V> 泛型
   */
  public static final class ExpiringValue<V> {

    /**
     * 不过期
     */
    private static final long NOT_EXPIRED = -1L;

    /**
     * 值
     */
    private final V value;

    /**
     * 过期时间
     */
    private long expireAt;

    /**
     * 构造不过期的包装
     *
     * @param value 值
     */
    ExpiringValue(V value) {
      this.value = value;
      this.expireAt = NOT_EXPIRED;
    }

    /**
     * 构造带过期时间的包装
     *
     * @param value 值
     * @param duration 期限
     * @param timeUnit 时间单位
     */
    ExpiringValue(V value, long duration, TimeUnit timeUnit) {
      this.value = value;
      if (duration > 0) {
        this.expireAt = System.currentTimeMillis() + timeUnit.toMillis(duration);
      } else {
        this.expireAt = NOT_EXPIRED;
      }
    }

    /**
     * 是否过期
     *
     * @return true是过期
     */
    boolean isExpired() {
      return expireAt != -1 && expireAt < System.currentTimeMillis();
    }

  }

  static final class Entry<K, V> implements Map.Entry<K, V> {

    K key;
    V value;

    Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V setValue(V value) {
      V pre = this.value;
      this.value = value;
      return pre;
    }
  }

}
