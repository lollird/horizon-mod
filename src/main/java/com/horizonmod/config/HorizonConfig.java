package com.horizonmod.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HorizonConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");
	
	// Rendering settings
	private int renderDistance = 1000; // chunks
	private int lodLevels = 8; // Number of LOD detail levels
	private boolean enableOpenGLOptimizations = true;
	private int targetFPS = 60;
	private boolean enableFrustumCulling = true;
	private boolean enableOcclusionCulling = false;
	
	// Performance settings
	private float lodDetailThreshold = 0.5f; // Distance at which to reduce detail
	private boolean adaptiveQuality = true; // Automatically adjust quality based on FPS
	private int maxDrawDistance = 1500; // Max distance before stopping rendering
	
	// Debug settings
	private boolean debugMode = false;
	private boolean showLODBoundaries = false;

	public void load() {
		// Configuration is now loaded via HorizonConfigManager
		LOGGER.info("Configuration loaded with render distance: {} chunks", renderDistance);
	}

	public void save() {
		// Configuration is now saved via HorizonConfigManager
		HorizonConfigManager.saveConfig(this);
	}

	// Getters
	public int getRenderDistance() {
		return renderDistance;
	}

	public int getLodLevels() {
		return lodLevels;
	}

	public boolean isOpenGLOptimizationsEnabled() {
		return enableOpenGLOptimizations;
	}

	public int getTargetFPS() {
		return targetFPS;
	}

	public boolean isFrustumCullingEnabled() {
		return enableFrustumCulling;
	}

	public boolean isOcclusionCullingEnabled() {
		return enableOcclusionCulling;
	}

	public float getLodDetailThreshold() {
		return lodDetailThreshold;
	}

	public boolean isAdaptiveQualityEnabled() {
		return adaptiveQuality;
	}

	public int getMaxDrawDistance() {
		return maxDrawDistance;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public boolean shouldShowLODBoundaries() {
		return showLODBoundaries;
	}

	// Setters
	public void setRenderDistance(int distance) {
		this.renderDistance = Math.max(32, Math.min(2048, distance)); // Clamp between 32 and 2048
		save();
	}

	public void setLodLevels(int levels) {
		this.lodLevels = Math.max(1, Math.min(16, levels));
		save();
	}

	public void setTargetFPS(int fps) {
		this.targetFPS = Math.max(30, Math.min(240, fps));
		save();
	}

	public void setAdaptiveQuality(boolean enabled) {
		this.adaptiveQuality = enabled;
		save();
	}

	public void setDebugMode(boolean enabled) {
		this.debugMode = enabled;
	}

	public void setShowLODBoundaries(boolean show) {
		this.showLODBoundaries = show;
	}
}
