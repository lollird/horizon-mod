package com.horizonmod.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Generic resource cache with time-to-live (TTL) support
 */
public class ResourceCache<K, V> {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private static final long DEFAULT_TTL_MS = 300000; // 5 minutes

	private final Map<K, CacheEntry<V>> cache = new LinkedHashMap<>();
	private final long ttlMs;
	private final int maxSize;
	private long hits = 0;
	private long misses = 0;

	public ResourceCache(long ttlMs, int maxSize) {
		this.ttlMs = ttlMs;
		this.maxSize = maxSize;
	}

	public ResourceCache(int maxSize) {
		this(DEFAULT_TTL_MS, maxSize);
	}

	/**
	 * Get value from cache
	 */
	public synchronized V get(K key) {
		CacheEntry<V> entry = cache.get(key);

		if (entry == null) {
			misses++;
			return null;
		}

		// Check if expired
		if (System.currentTimeMillis() - entry.createdAt > ttlMs) {
			cache.remove(key);
			misses++;
			return null;
		}

		hits++;
		entry.lastAccessedAt = System.currentTimeMillis();
		return entry.value;
	}

	/**
	 * Put value into cache
	 */
	public synchronized void put(K key, V value) {
		// Evict oldest entry if cache is full
		if (cache.size() >= maxSize) {
			K oldestKey = cache.keySet().iterator().next();
			cache.remove(oldestKey);
		}

		cache.put(key, new CacheEntry<>(value));
	}

	/**
	 * Remove value from cache
	 */
	public synchronized void remove(K key) {
		cache.remove(key);
	}

	/**
	 * Clear all cache
	 */
	public synchronized void clear() {
		cache.clear();
	}

	/**
	 * Cleanup expired entries
	 */
	public synchronized void cleanup() {
		long currentTime = System.currentTimeMillis();
		int removed = 0;

		cache.entrySet().removeIf(entry -> {
			if (currentTime - entry.getValue().createdAt > ttlMs) {
				removed++;
				return true;
			}
			return false;
		});

		if (removed > 0) {
			LOGGER.debug("Cleaned up {} expired cache entries", removed);
		}
	}

	/**
	 * Get cache statistics
	 */
	public synchronized CacheStats getStats() {
		long totalRequests = hits + misses;
		double hitRate = totalRequests > 0 ? (hits * 100.0) / totalRequests : 0;
		return new CacheStats(cache.size(), hits, misses, hitRate);
	}

	/**
	 * Cache entry with TTL
	 */
	private static class CacheEntry<V> {
		private final V value;
		private final long createdAt;
		private long lastAccessedAt;

		CacheEntry(V value) {
			this.value = value;
			this.createdAt = System.currentTimeMillis();
			this.lastAccessedAt = createdAt;
		}
	}

	/**
	 * Cache statistics
	 */
	public static class CacheStats {
		public final int entriesInCache;
		public final long cacheHits;
		public final long cacheMisses;
		public final double hitRate;

		public CacheStats(int entries, long hits, long misses, double hitRate) {
			this.entriesInCache = entries;
			this.cacheHits = hits;
			this.cacheMisses = misses;
			this.hitRate = hitRate;
		}

		@Override
		public String toString() {
			return String.format("Cache: %d entries, Hit Rate: %.1f%% (%d hits, %d misses)",
				entriesInCache, hitRate, cacheHits, cacheMisses);
		}
	}
}
