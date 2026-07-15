package com.horizonmod.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Garbage collection optimization and monitoring
 */
public class GarbageCollectionOptimizer {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final long initialHeapMax;
	private long lastGCTime = System.currentTimeMillis();
	private long gcCount = 0;
	private long totalGCTime = 0;

	public GarbageCollectionOptimizer() {
		this.initialHeapMax = Runtime.getRuntime().maxMemory();
		optimizeGCSettings();
	}

	/**
	 * Apply optimal GC settings for Minecraft
	 */
	private void optimizeGCSettings() {
		try {
			// These are best-effort recommendations logged for user
			LOGGER.info("GC Optimization Settings:");
			LOGGER.info("  Recommended JVM Args:");
			LOGGER.info("  -XX:+UseG1GC");
			LOGGER.info("  -XX:MaxGCPauseMillis=200");
			LOGGER.info("  -XX:InitiatingHeapOccupancyPercent=35");
			LOGGER.info("  Heap: " + (initialHeapMax / (1024 * 1024)) + "MB");
		} catch (Exception e) {
			LOGGER.error("Error optimizing GC settings", e);
		}
	}

	/**
	 * Monitor GC activity
	 */
	public void monitorGC() {
		long currentGCCount = getGarbageCollectionCount();
		if (currentGCCount > gcCount) {
			gcCount = currentGCCount;
			lastGCTime = System.currentTimeMillis();
		}
	}

	/**
	 * Get heap memory statistics
	 */
	public HeapStats getHeapStats() {
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;

		return new HeapStats(maxMemory, totalMemory, usedMemory, freeMemory);
	}

	/**
	 * Request garbage collection
	 */
	public void suggestGarbageCollection() {
		System.gc();
		LOGGER.debug("Garbage collection suggested");
	}

	/**
	 * Get total GC time in milliseconds
	 */
	public long getTotalGCTimeMs() {
		return totalGCTime;
	}

	/**
	 * Get time since last GC
	 */
	public long getTimeSinceLastGC() {
		return System.currentTimeMillis() - lastGCTime;
	}

	/**
	 * Get GC count
	 */
	public long getGCCount() {
		return gcCount;
	}

	private long getGarbageCollectionCount() {
		return java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()
			.stream()
			.mapToLong(java.lang.management.GarbageCollectorMXBean::getCollectionCount)
			.sum();
	}

	/**
	 * Heap memory statistics
	 */
	public static class HeapStats {
		public final long maxMemoryBytes;
		public final long totalMemoryBytes;
		public final long usedMemoryBytes;
		public final long freeMemoryBytes;

		public HeapStats(long max, long total, long used, long free) {
			this.maxMemoryBytes = max;
			this.totalMemoryBytes = total;
			this.usedMemoryBytes = used;
			this.freeMemoryBytes = free;
		}

		public double getHeapUsagePercent() {
			return (usedMemoryBytes * 100.0) / maxMemoryBytes;
		}

		@Override
		public String toString() {
			return String.format("Heap: %d/%dMB (%.1f%%)",
				usedMemoryBytes / (1024 * 1024),
				maxMemoryBytes / (1024 * 1024),
				getHeapUsagePercent());
		}
	}
}
