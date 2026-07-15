package com.horizonmod.rendering.lod;

import com.horizonmod.config.HorizonConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LODManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");
	
	private final HorizonConfig config;
	private int currentLODQuality = 100; // 0-100 quality scale
	private final int[] lodDistances; // Distance thresholds for each LOD level

	public LODManager(HorizonConfig config) {
		this.config = config;
		this.lodDistances = calculateLODDistances();
		LOGGER.info("LOD Manager initialized with {} quality levels", config.getLodLevels());
	}

	/**
	 * Calculate distance thresholds for each LOD level
	 */
	private int[] calculateLODDistances() {
		int[] distances = new int[config.getLodLevels()];
		int maxDistance = config.getMaxDrawDistance();
		
		for (int i = 0; i < distances.length; i++) {
			// Exponential distribution: closer chunks get more detail
			distances[i] = (int) (maxDistance * Math.pow(2.0, (double) i / (distances.length - 1)) / 2);
		}
		
		return distances;
	}

	/**
	 * Update LOD state based on camera position (can be called once per frame)
	 */
	public void updateLODState() {
		// TODO: Update based on camera frustum and position
	}

	/**
	 * Calculate LOD level based on distance from camera
	 */
	public int calculateLODLevel(double distance) {
		// Apply quality scaling
		distance = distance * (100.0 / currentLODQuality);
		
		for (int i = 0; i < lodDistances.length; i++) {
			if (distance < lodDistances[i]) {
				return i;
			}
		}
		
		return lodDistances.length - 1; // Return highest LOD (lowest detail)
	}

	/**
	 * Reduce LOD quality (lower FPS, higher quality)
	 */
	public void reduceLODQuality() {
		int newQuality = Math.max(50, currentLODQuality - 5);
		if (newQuality != currentLODQuality) {
			currentLODQuality = newQuality;
			LOGGER.debug("Reduced LOD quality to {}", currentLODQuality);
		}
	}

	/**
	 * Increase LOD quality (higher FPS, lower quality)
	 */
	public void increaseLODQuality() {
		int newQuality = Math.min(100, currentLODQuality + 5);
		if (newQuality != currentLODQuality) {
			currentLODQuality = newQuality;
			LOGGER.debug("Increased LOD quality to {}", currentLODQuality);
		}
	}

	// Getters
	public int getCurrentLODQuality() {
		return currentLODQuality;
	}

	public int[] getLODDistances() {
		return lodDistances;
	}
}
