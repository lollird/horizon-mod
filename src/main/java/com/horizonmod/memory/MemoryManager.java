package com.horizonmod.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Memory pool manager for efficient allocation and reuse
 */
public class MemoryManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final Map<Integer, ObjectPool<?>> pools = new HashMap<>();
	private long totalAllocated = 0;
	private long totalReused = 0;

	/**
	 * Get or create a pool for a specific type
	 */
	@SuppressWarnings("unchecked")
	public <T> ObjectPool<T> getPool(Class<T> type, int initialSize) {
		int hash = type.hashCode();
		return (ObjectPool<T>) pools.computeIfAbsent(hash, k -> {
			ObjectPool<T> pool = new ObjectPool<>(initialSize, type);
			LOGGER.debug("Created object pool for {} with initial size {}", type.getSimpleName(), initialSize);
			return pool;
		});
	}

	/**
	 * Allocate memory from pool or create new
	 */
	@SuppressWarnings("unchecked")
	public <T> T allocate(Class<T> type) {
		int hash = type.hashCode();
		if (pools.containsKey(hash)) {
			ObjectPool<T> pool = (ObjectPool<T>) pools.get(hash);
			T obj = pool.acquire();
			if (obj != null) {
				totalReused++;
				return obj;
			}
		}

		try {
			totalAllocated++;
			return type.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			LOGGER.error("Failed to allocate {}", type.getSimpleName(), e);
			return null;
		}
	}

	/**
	 * Return object to pool
	 */
	@SuppressWarnings("unchecked")
	public <T> void release(T object) {
		if (object == null) return;

		int hash = object.getClass().hashCode();
		if (pools.containsKey(hash)) {
			ObjectPool<T> pool = (ObjectPool<T>) pools.get(hash);
			pool.release(object);
		}
	}

	/**
	 * Get memory statistics
	 */
	public MemoryStats getStats() {
		long poolSize = pools.values().stream()
			.mapToLong(pool -> (long) pool.available() * 1024) // Rough estimate
			.sum();

		return new MemoryStats(totalAllocated, totalReused, poolSize);
	}

	/**
	 * Clear all pools
	 */
	public void clear() {
		pools.clear();
		totalAllocated = 0;
		totalReused = 0;
		LOGGER.info("Cleared all memory pools");
	}

	/**
	 * Object pool implementation
	 */
	public static class ObjectPool<T> {
		private final Queue<T> available;
		private final Set<T> inUse;
		private final Class<T> type;
		private final int maxSize;

		public ObjectPool(int initialSize, Class<T> type) {
			this.available = new LinkedList<>();
			this.inUse = new HashSet<>();
			this.type = type;
			this.maxSize = initialSize * 2;

			// Pre-allocate objects
			for (int i = 0; i < initialSize; i++) {
				try {
					available.offer(type.getDeclaredConstructor().newInstance());
				} catch (Exception e) {
					// Ignore allocation errors
				}
			}
		}

		public synchronized T acquire() {
			T obj = available.poll();
			if (obj != null) {
				inUse.add(obj);
			}
			return obj;
		}

		public synchronized void release(T obj) {
			inUse.remove(obj);
			if (available.size() < maxSize) {
				available.offer(obj);
			}
		}

		public int available() {
			return available.size();
		}

		public int inUse() {
			return inUse.size();
		}
	}

	/**
	 * Memory statistics
	 */
	public static class MemoryStats {
		public final long totalAllocated;
		public final long totalReused;
		public final long estimatedPoolSize;

		public MemoryStats(long allocated, long reused, long poolSize) {
			this.totalAllocated = allocated;
			this.totalReused = reused;
			this.estimatedPoolSize = poolSize;
		}

		public double getReusePercentage() {
			if (totalAllocated == 0) return 0;
			return (totalReused * 100.0) / (totalAllocated + totalReused);
		}
	}
}
