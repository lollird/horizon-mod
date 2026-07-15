package com.horizonmod.rendering.chunk;

import net.minecraft.util.math.ChunkPos;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a simplified LOD version of a chunk for efficient rendering
 */
public class LODChunk {
	private final ChunkPos chunkPos;
	private final int lodLevel;
	private final SimplifiedGeometry geometry;
	private boolean needsUpdate = true;
	private long lastUpdateTime = 0;

	public LODChunk(ChunkPos chunkPos, int lodLevel) {
		this.chunkPos = chunkPos;
		this.lodLevel = lodLevel;
		this.geometry = new SimplifiedGeometry(lodLevel);
	}

	/**
	 * Update this LOD chunk's geometry based on terrain data
	 */
	public void updateGeometry(ChunkTerrainData terrainData) {
		if (!this.needsUpdate) {
			return;
		}

		switch (lodLevel) {
			case 0:
				// Full detail - use all blocks
				geometry.generateFullDetail(terrainData);
				break;
			case 1:
				// Half detail - sample every 2 blocks
				geometry.generateReducedDetail(terrainData, 2);
				break;
			case 2:
				// Quarter detail - sample every 4 blocks
				geometry.generateReducedDetail(terrainData, 4);
				break;
			case 3:
				// Eighth detail - sample every 8 blocks
				geometry.generateReducedDetail(terrainData, 8);
				break;
			case 4:
			case 5:
				// Very low detail - sample every 16 blocks
				geometry.generateReducedDetail(terrainData, 16);
				break;
			default:
				// Extremely low detail - just use heightmap
				geometry.generateHeightmapOnly(terrainData);
		}

		this.needsUpdate = false;
		this.lastUpdateTime = System.currentTimeMillis();
	}

	/**
	 * Mark this chunk as needing a geometry update
	 */
	public void markDirty() {
		this.needsUpdate = true;
	}

	public ChunkPos getChunkPos() {
		return chunkPos;
	}

	public int getLODLevel() {
		return lodLevel;
	}

	public SimplifiedGeometry getGeometry() {
		return geometry;
	}

	public boolean needsUpdate() {
		return needsUpdate;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * Internal class for storing simplified geometry data
	 */
	public static class SimplifiedGeometry {
		private final int lodLevel;
		private int vertexCount = 0;
		private float[] heightmap; // Simplified heightmap
		private int[] blockTypes; // Simplified block type data

		public SimplifiedGeometry(int lodLevel) {
			this.lodLevel = lodLevel;
		}

		/**
		 * Generate full detail geometry (no simplification)
		 */
		public void generateFullDetail(ChunkTerrainData terrainData) {
			// For LOD level 0, we use the original chunk data
			this.vertexCount = terrainData.getTotalVertices();
			this.heightmap = terrainData.getHeightmap();
			this.blockTypes = terrainData.getBlockTypes();
		}

		/**
		 * Generate reduced detail by sampling terrain at intervals
		 */
		public void generateReducedDetail(ChunkTerrainData terrainData, int sampleRate) {
			int samples = 16 / sampleRate; // How many samples per dimension
			this.vertexCount = samples * samples * 4; // Approximate vertex count

			// Sample heightmap at intervals
			float[] originalHeightmap = terrainData.getHeightmap();
			this.heightmap = new float[samples * samples];

			for (int i = 0; i < samples; i++) {
				for (int j = 0; j < samples; j++) {
					int originalIndex = (i * sampleRate) * 16 + (j * sampleRate);
					if (originalIndex < originalHeightmap.length) {
						this.heightmap[i * samples + j] = originalHeightmap[originalIndex];
					}
				}
			}

			int[] originalBlockTypes = terrainData.getBlockTypes();
			this.blockTypes = new int[samples * samples];

			for (int i = 0; i < samples; i++) {
				for (int j = 0; j < samples; j++) {
					int originalIndex = (i * sampleRate) * 16 + (j * sampleRate);
					if (originalIndex < originalBlockTypes.length) {
						this.blockTypes[i * samples + j] = originalBlockTypes[originalIndex];
					}
				}
			}
		}

		/**
		 * Generate heightmap-only representation (minimal detail)
		 */
		public void generateHeightmapOnly(ChunkTerrainData terrainData) {
			int gridSize = 8; // 8x8 grid for extreme distances
			this.vertexCount = gridSize * gridSize * 2; // 2 triangles per quad

			float[] originalHeightmap = terrainData.getHeightmap();
			this.heightmap = new float[gridSize * gridSize];

			// Sample heightmap at wide intervals
			int sampleRate = 16 / gridSize;
			for (int i = 0; i < gridSize; i++) {
				for (int j = 0; j < gridSize; j++) {
					int originalIndex = (i * sampleRate) * 16 + (j * sampleRate);
					if (originalIndex < originalHeightmap.length) {
						this.heightmap[i * gridSize + j] = originalHeightmap[originalIndex];
					}
				}
			}
		}

		public int getVertexCount() {
			return vertexCount;
		}

		public float[] getHeightmap() {
			return heightmap;
		}

		public int[] getBlockTypes() {
			return blockTypes;
		}
	}
}
