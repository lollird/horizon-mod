package com.horizonmod.rendering.chunk;

import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simplification strategy for different LOD levels
 */
public class GeometrySimplifier {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	/**
	 * Calculate compression ratio for a given LOD level
	 * LOD 0 = 1.0 (no compression)
	 * LOD 8 = 0.015625 (1/64th detail)
	 */
	public static float getCompressionRatio(int lodLevel) {
		if (lodLevel <= 0) return 1.0f;
		return 1.0f / (float) Math.pow(2, lodLevel);
	}

	/**
	 * Get sampling rate for terrain simplification
	 * LOD 0 = 1 (sample every block)
	 * LOD 3 = 8 (sample every 8 blocks)
	 */
	public static int getSamplingRate(int lodLevel) {
		if (lodLevel <= 0) return 1;
		return (int) Math.pow(2, lodLevel);
	}

	/**
	 * Calculate estimated vertex count after simplification
	 */
	public static int estimateSimplifiedVertices(int originalVertices, int lodLevel) {
		float compressionRatio = getCompressionRatio(lodLevel);
		return Math.max(8, (int) (originalVertices * compressionRatio));
	}

	/**
	 * Calculate memory savings from simplification
	 */
	public static MemorySavings calculateMemorySavings(int chunksInCache, int lodLevel) {
		// Rough estimates: ~50KB per chunk at full detail
		int bytesPerFullChunk = 50000;
		float compressionRatio = getCompressionRatio(lodLevel);

		long originalSize = (long) chunksInCache * bytesPerFullChunk;
		long simplifiedSize = (long) (originalSize * compressionRatio);
		long savedBytes = originalSize - simplifiedSize;

		return new MemorySavings(originalSize, simplifiedSize, savedBytes);
	}

	/**
	 * Memory usage statistics
	 */
	public static class MemorySavings {
		public final long originalBytes;
		public final long simplifiedBytes;
		public final long savedBytes;

		public MemorySavings(long original, long simplified, long saved) {
			this.originalBytes = original;
			this.simplifiedBytes = simplified;
			this.savedBytes = saved;
		}

		public double getCompressionPercentage() {
			return (savedBytes * 100.0) / originalBytes;
		}

		@Override
		public String toString() {
			return String.format("Original: %d MB, Simplified: %d MB, Saved: %.1f%%",
				originalBytes / (1024 * 1024),
				simplifiedBytes / (1024 * 1024),
				getCompressionPercentage());
		}
	}
}
