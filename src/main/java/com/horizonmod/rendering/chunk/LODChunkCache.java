package com.horizonmod.rendering.chunk;

import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manages LOD chunk cache and simplification
 */
public class LODChunkCache {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");
	private static final int MAX_CACHED_CHUNKS = 2048; // Maximum chunks to keep in memory
	private static final long CHUNK_CACHE_TIMEOUT = 300000; // 5 minutes

	private final Map<ChunkPos, LODChunk> lodChunks = new HashMap<>();
	private int totalSimplifiedVertices = 0;

	/**
	 * Get or create an LOD chunk at the specified coordinates
	 */
	public LODChunk getOrCreateLODChunk(ChunkPos chunkPos, int lodLevel) {
		return lodChunks.computeIfAbsent(chunkPos, pos -> {
			LODChunk chunk = new LODChunk(pos, lodLevel);

				// Purge old chunks if cache is too large
			if (lodChunks.size() > MAX_CACHED_CHUNKS) {
				purgeOldChunks();
			}

			return chunk;
		});
	}

	/**
	 * Update a chunk's LOD geometry
	 */
	public void updateChunkLOD(ChunkPos chunkPos, int lodLevel) {
		LODChunk lodChunk = getOrCreateLODChunk(chunkPos, lodLevel);

		if (lodChunk.needsUpdate()) {
			ChunkTerrainData terrainData = ChunkTerrainData.fromChunkCoords(
				chunkPos.x, chunkPos.z);
			lodChunk.updateGeometry(terrainData);

			int oldVertexCount = totalSimplifiedVertices;
			totalSimplifiedVertices = lodChunks.values().stream()
				.mapToInt(c -> c.getGeometry().getVertexCount())
				.sum();
		}
	}

	/**
	 * Purge chunks older than cache timeout
	 */
	private void purgeOldChunks() {
		long currentTime = System.currentTimeMillis();
		Iterator<Map.Entry<ChunkPos, LODChunk>> iterator = lodChunks.entrySet().iterator();

		int purgedCount = 0;
		while (iterator.hasNext()) {
			Map.Entry<ChunkPos, LODChunk> entry = iterator.next();
			LODChunk chunk = entry.getValue();

			if (currentTime - chunk.getLastUpdateTime() > CHUNK_CACHE_TIMEOUT) {
				iterator.remove();
				purgedCount++;
			}

			if (lodChunks.size() <= MAX_CACHED_CHUNKS * 0.8) {
				break; // Reduce to 80% capacity
			}
		}

		if (purgedCount > 0) {
			LOGGER.debug("Purged {} old LOD chunks from cache", purgedCount);
		}
	}

	/**
	 * Clear all cached chunks
	 */
	public void clear() {
		lodChunks.clear();
		totalSimplifiedVertices = 0;
		LOGGER.info("Cleared LOD chunk cache");
	}

	/**
	 * Get cache statistics
	 */
	public CacheStats getStats() {
		return new CacheStats(lodChunks.size(), totalSimplifiedVertices);
	}

	/**
	 * Statistics about the cache
	 */
	public static class CacheStats {
		public final int chunksInCache;
		public final int totalVertices;

		public CacheStats(int chunksInCache, int totalVertices) {
			this.chunksInCache = chunksInCache;
			this.totalVertices = totalVertices;
		}
	}
}
