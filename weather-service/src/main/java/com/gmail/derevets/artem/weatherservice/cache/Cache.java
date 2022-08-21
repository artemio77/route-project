package com.gmail.derevets.artem.weatherservice.cache;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

public interface Cache<K, V> {

    /**
     * Store key and value in cache with provided ttl
     *
     * @param key     map key
     * @param value   map value
     * @param ttl     to preserve entry in the cache. If 0 then time to live doesn't affect entry expiration.
     * @param ttlUnit time unit
     */
    void store(K key, V value, long ttl, ChronoUnit ttlUnit);

    /**
     * If the specified key is not already associated with a value, associate it with the given value.
     *
     * @param key     map key
     * @param value   map value
     * @param ttl     to preserve entry in the cache. If 0 then time to live doesn't affect entry expiration.
     * @param ttlUnit time unit
     * @return true if key is a new key in the hash and value was set and false if key already exists in the hash
     */
    boolean putIfAbsent(K key, V value, long ttl, ChronoUnit ttlUnit);

    /**
     * Check if there are value by provided key
     *
     * @param key map key
     */
    boolean isPresent(K key);

    /**
     * Return value by provided key
     *
     * @param key map key
     */
    Optional<V> get(K key);

}
