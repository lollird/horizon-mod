package com.horizonmod.rendering.chunk;

/**
 * Terrain data extracted from a Minecraft chunk
 */
public class ChunkTerrainData {
	private final float[] heightmap; // Height values for the chunk
	private final int[] blockTypes; // Block type IDs
	private final int totalVertices;

	public ChunkTerrainData(float[] heightmap, int[] blockTypes) {
		this.heightmap = heightmap;
		this.blockTypes = blockTypes;
		this.totalVertices = heightmap.length * 4; // Estimate: 4 verts per height point
	}

	/**
	 * Create terrain data from chunk at specific X/Z coordinates
	 */
	public static ChunkTerrainData fromChunkCoords(int chunkX, int chunkZ) {
		// This will be populated when we have world access
		// For now, return dummy data
		float[] heightmap = new float[256]; // 16x16
		int[] blockTypes = new int[256];

		// Initialize with default values
		for (int i = 0; i < 256; i++) {
			heightmap[i] = 64f; // Default height
			blockTypes[i] = 1; // Stone block ID
		}

		return new ChunkTerrainData(heightmap, blockTypes);
	}

	public float[] getHeightmap() {
		return heightmap;
	}

	public int[] getBlockTypes() {
		return blockTypes;
	}

	public int getTotalVertices() {
		return totalVertices;
	}
}
