package com.horizonmod.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Texture atlas for efficient texture packing and management
 */
public class TextureAtlas {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");

	private static final int ATLAS_SIZE = 2048; // 2048x2048 texture
	private static final int TEXTURE_SIZE = 16; // 16x16 individual textures
	private static final int TEXTURES_PER_ATLAS = (ATLAS_SIZE / TEXTURE_SIZE) * (ATLAS_SIZE / TEXTURE_SIZE);

	private final Map<String, TextureCoords> textureMap = new HashMap<>();
	private final boolean[][] occupancyGrid;
	private int textureCount = 0;

	public TextureAtlas() {
		this.occupancyGrid = new boolean[ATLAS_SIZE / TEXTURE_SIZE][ATLAS_SIZE / TEXTURE_SIZE];
		LOGGER.info("Texture Atlas initialized: {}x{} with {} slots",
			ATLAS_SIZE, ATLAS_SIZE, TEXTURES_PER_ATLAS);
	}

	/**
	 * Register a texture in the atlas
	 */
	public synchronized TextureCoords registerTexture(String name) {
		if (textureMap.containsKey(name)) {
			return textureMap.get(name);
		}

		if (textureCount >= TEXTURES_PER_ATLAS) {
			LOGGER.warn("Texture atlas full, cannot add texture: {}", name);
			return null;
		}

		// Find next available slot
		for (int y = 0; y < occupancyGrid.length; y++) {
			for (int x = 0; x < occupancyGrid[y].length; x++) {
				if (!occupancyGrid[y][x]) {
					occupancyGrid[y][x] = true;
					TextureCoords coords = new TextureCoords(x, y, ATLAS_SIZE, TEXTURE_SIZE);
					textureMap.put(name, coords);
					textureCount++;
					return coords;
				}
			}
		}

		return null;
	}

	/**
	 * Get texture coordinates
	 */
	public synchronized TextureCoords getTexture(String name) {
		return textureMap.get(name);
	}

	/**
	 * Get atlas statistics
	 */
	public synchronized AtlasStats getStats() {
		return new AtlasStats(textureCount, TEXTURES_PER_ATLAS, (textureCount * 100.0) / TEXTURES_PER_ATLAS);
	}

	/**
	 * Texture coordinates within atlas
	 */
	public static class TextureCoords {
		public final float minU;
		public final float minV;
		public final float maxU;
		public final float maxV;

		public TextureCoords(int gridX, int gridY, int atlasSize, int textureSize) {
			this.minU = (gridX * textureSize) / (float) atlasSize;
			this.minV = (gridY * textureSize) / (float) atlasSize;
			this.maxU = ((gridX + 1) * textureSize) / (float) atlasSize;
			this.maxV = ((gridY + 1) * textureSize) / (float) atlasSize;
		}

		@Override
		public String toString() {
			return String.format("UV[%.3f,%.3f - %.3f,%.3f]", minU, minV, maxU, maxV);
		}
	}

	/**
	 * Atlas statistics
	 */
	public static class AtlasStats {
		public final int texturesUsed;
		public final int texturesAvailable;
		public final double utilizationPercent;

		public AtlasStats(int used, int available, double utilization) {
			this.texturesUsed = used;
			this.texturesAvailable = available;
			this.utilizationPercent = utilization;
		}

		@Override
		public String toString() {
			return String.format("Texture Atlas: %d/%d textures (%.1f%% utilized)",
				texturesUsed, texturesAvailable, utilizationPercent);
		}
	}
}
