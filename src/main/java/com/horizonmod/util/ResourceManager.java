package com.horizonmod.util;

import com.horizonmod.memory.MemoryManager;
import com.horizonmod.memory.ResourceCache;
import com.horizonmod.memory.BufferPool;
import com.horizonmod.memory.TextureAtlas;
import com.horizonmod.memory.GarbageCollectionOptimizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized memory and resource management
 */
public class ResourceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final MemoryManager memoryManager;
	private final ResourceCache<String, Object> resourceCache;
	private final BufferPool bufferPool;
	private final TextureAtlas textureAtlas;
	private final GarbageCollectionOptimizer gcOptimizer;

	public ResourceManager() {
		this.memoryManager = new MemoryManager();
		this.resourceCache = new ResourceCache<>(5 * 60 * 1000, 1000); // 5 min TTL, 1000 entries
		this.bufferPool = new BufferPool();
		this.textureAtlas = new TextureAtlas();
		this.gcOptimizer = new GarbageCollectionOptimizer();

		LOGGER.info("Resource Manager initialized");
	}

	/**
	 * Perform cleanup and optimization
	 */
	public void optimize() {
		// Clean up expired cache entries
		resourceCache.cleanup();

		// Monitor GC
		gcOptimizer.monitorGC();

		// Suggest GC if heap usage is high
		var heapStats = gcOptimizer.getHeapStats();
		if (heapStats.getHeapUsagePercent() > 80) {
			LOGGER.debug("Heap usage at {}, suggesting GC", heapStats);
			gcOptimizer.suggestGarbageCollection();
		}
	}

	// Getters
	public MemoryManager getMemoryManager() {
		return memoryManager;
	}

	public ResourceCache<String, Object> getResourceCache() {
		return resourceCache;
	}

	public BufferPool getBufferPool() {
		return bufferPool;
	}

	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}

	public GarbageCollectionOptimizer getGCOptimizer() {
		return gcOptimizer;
	}

	/**
	 * Get comprehensive resource statistics
	 */
	public String getResourceStats() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n=== Resource Statistics ===");
		sb.append("\n").append(gcOptimizer.getHeapStats());
		sb.append("\n").append(bufferPool.getStats());
		sb.append("\n").append(textureAtlas.getStats());
		sb.append("\n").append(resourceCache.getStats());
		sb.append("\n=======================\n");
		return sb.toString();
	}

	/**
	 * Cleanup all resources
	 */
	public void cleanup() {
		bufferPool.cleanup();
		resourceCache.clear();
		memoryManager.clear();
		LOGGER.info("Resource Manager cleaned up");
	}
}
