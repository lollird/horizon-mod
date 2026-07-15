package com.horizonmod.rendering;

import com.horizonmod.config.HorizonConfig;
import com.horizonmod.rendering.lod.LODManager;
import com.horizonmod.rendering.opengl.OpenGLOptimizer;
import com.horizonmod.rendering.culling.FrustumCuller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LODRenderSystem {
	private static final Logger LOGGER = LoggerFactory.getLogger("horizonmod");
	
	private final HorizonConfig config;
	private final LODManager lodManager;
	private final OpenGLOptimizer openGLOptimizer;
	private final FrustumCuller frustumCuller;
	
	private long lastFrameTime = System.currentTimeMillis();
	private int frameCount = 0;
	private double currentFPS = 60.0;

	public LODRenderSystem(HorizonConfig config) {
		this.config = config;
		this.lodManager = new LODManager(config);
		this.openGLOptimizer = new OpenGLOptimizer(config);
		this.frustumCuller = new FrustumCuller();
		
		LOGGER.info("LOD Render System initialized");
		LOGGER.info("- Render Distance: {} chunks", config.getRenderDistance());
		LOGGER.info("- LOD Levels: {}", config.getLodLevels());
		LOGGER.info("- Target FPS: {}", config.getTargetFPS());
		LOGGER.info("- Frustum Culling: {}", config.isFrustumCullingEnabled());
	}

	/**
	 * Called every frame to update LOD state and prepare rendering
	 */
	public void onRenderTick() {
		updateFPS();
		
		// Update LOD levels based on camera position
		lodManager.updateLODState();
		
		// Update frustum culling if enabled
		if (config.isFrustumCullingEnabled()) {
			frustumCuller.updateFrustum();
		}
		
		// Adaptive quality adjustment
		if (config.isAdaptiveQualityEnabled()) {
			adjustQualityForFPS();
		}
	}

	/**
	 * Calculate current FPS for performance monitoring
	 */
	private void updateFPS() {
		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - lastFrameTime;
		
		if (deltaTime > 0) {
			currentFPS = 1000.0 / deltaTime;
		}
		
		frameCount++;
		lastFrameTime = currentTime;
	}

	/**
	 * Adjust rendering quality based on FPS to maintain target FPS
	 */
	private void adjustQualityForFPS() {
		if (currentFPS < config.getTargetFPS() * 0.9) {
			// FPS is dropping, reduce quality
			lodManager.reduceLODQuality();
		} else if (currentFPS > config.getTargetFPS() * 1.1) {
			// FPS is good, can increase quality
			lodManager.increaseLODQuality();
		}
	}

	/**
	 * Check if a chunk should be rendered at current LOD
	 */
	public boolean shouldRenderChunk(int chunkX, int chunkZ, double cameraX, double cameraZ) {
		// Check render distance
		double distanceSquared = Math.pow(chunkX * 16 - cameraX, 2) + Math.pow(chunkZ * 16 - cameraZ, 2);
		double maxDistanceSquared = Math.pow(config.getRenderDistance() * 16, 2);
		
		if (distanceSquared > maxDistanceSquared) {
			return false;
		}
		
		// Check frustum culling
		if (config.isFrustumCullingEnabled()) {
			return frustumCuller.isChunkInFrustum(chunkX, chunkZ);
		}
		
		return true;
	}

	/**
	 * Get the LOD level for a chunk based on distance
	 */
	public int getLODLevel(int chunkX, int chunkZ, double cameraX, double cameraZ) {
		double distance = Math.sqrt(
			Math.pow(chunkX * 16 - cameraX, 2) + 
			Math.pow(chunkZ * 16 - cameraZ, 2)
		);
		
		return lodManager.calculateLODLevel(distance);
	}

	// Getters
	public double getCurrentFPS() {
		return currentFPS;
	}

	public LODManager getLODManager() {
		return lodManager;
	}

	public OpenGLOptimizer getOpenGLOptimizer() {
		return openGLOptimizer;
	}

	public FrustumCuller getFrustumCuller() {
		return frustumCuller;
	}

	public HorizonConfig getConfig() {
		return config;
	}
}
