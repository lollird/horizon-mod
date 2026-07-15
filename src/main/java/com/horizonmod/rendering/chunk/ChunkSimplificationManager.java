package com.horizonmod.rendering.chunk;

import com.horizonmod.HorizonModClient;
import com.horizonmod.config.HorizonConfig;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Orchestrates chunk simplification and LOD updates
 */
public class ChunkSimplificationManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private final LODChunkCache cache;
	private final HorizonConfig config;
	private int lastProcessedChunks = 0;

	public ChunkSimplificationManager(HorizonConfig config) {
		this.config = config;
		this.cache = new LODChunkCache();
		LOGGER.info("Chunk Simplification Manager initialized");
	}

	/**
	 * Process chunks within render distance and apply LOD
	 */
	public void processCulledChunks(double cameraX, double cameraZ) {
		int renderDistance = config.getRenderDistance();
		int chunkX = (int) cameraX / 16;
		int chunkZ = (int) cameraZ / 16;

		int chunkCount = 0;

		// Iterate through all chunks within render distance
		for (int x = chunkX - renderDistance; x <= chunkX + renderDistance; x++) {
			for (int z = chunkZ - renderDistance; z <= chunkZ + renderDistance; z++) {
				ChunkPos pos = new ChunkPos(x, z);
				double distanceSquared = Math.pow(x * 16 - cameraX, 2) + Math.pow(z * 16 - cameraZ, 2);

				// Skip if outside render distance
				if (distanceSquared > Math.pow(renderDistance * 16, 2)) {
					continue;
				}

				// Get LOD level for this chunk
				int lodLevel = HorizonModClient.getLODRenderSystem()
					.getLODLevel(x, z, cameraX, cameraZ);

				// Update or create LOD chunk
				cache.updateChunkLOD(pos, lodLevel);
				chunkCount++;
			}
		}

		if (config.isDebugMode() && chunkCount != lastProcessedChunks) {
			LOGGER.info("Processing {} chunks with {} LOD chunks cached",
				chunkCount, cache.getStats().chunksInCache);
			lastProcessedChunks = chunkCount;
		}
	}

	/**
	 * Get cache statistics for debugging
	 */
	public LODChunkCache.CacheStats getCacheStats() {
		return cache.getStats();
	}

	/**
	 * Clear all cached chunks (when world unloads)
	 */
	public void clear() {
		cache.clear();
	}
}
