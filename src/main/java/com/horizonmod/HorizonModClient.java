package com.horizonmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import com.horizonmod.rendering.LODRenderSystem;
import com.horizonmod.config.HorizonConfig;
import com.horizonmod.config.HorizonConfigManager;
import com.horizonmod.screen.ScreenEventHandler;
import com.horizonmod.util.PerformanceMonitor;
import com.horizonmod.util.DebugOverlay;
import com.horizonmod.util.MetricsCollector;
import com.horizonmod.util.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class HorizonModClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(HorizonMod.MOD_ID);
	private static LODRenderSystem lodRenderSystem;
	private static HorizonConfig config;
	private static PerformanceMonitor performanceMonitor;
	private static DebugOverlay debugOverlay;
	private static MetricsCollector metricsCollector;
	private static ResourceManager resourceManager;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Horizon Mod Client initialized!");
		
		// Initialize configuration
		config = new HorizonConfig();
		HorizonConfigManager.loadConfig(config);
		
		// Initialize resource management
		resourceManager = new ResourceManager();
		
		// Initialize performance monitoring
		performanceMonitor = new PerformanceMonitor();
		metricsCollector = new MetricsCollector(performanceMonitor);
		debugOverlay = new DebugOverlay(performanceMonitor);
		
		// Initialize LOD rendering system
		lodRenderSystem = new LODRenderSystem(config);
		LOGGER.info("LOD Render System initialized with render distance: {} chunks", config.getRenderDistance());
		
		// Register key bindings and screen events
		ScreenEventHandler.registerKeyInputs();
		ScreenEventHandler.setDebugOverlay(debugOverlay);
		LOGGER.info("Key bindings registered (Press 'H' for config, 'G' for debug overlay)");
	}

	public static LODRenderSystem getLODRenderSystem() {
		return lodRenderSystem;
	}

	public static HorizonConfig getConfig() {
		return config;
	}

	public static PerformanceMonitor getPerformanceMonitor() {
		return performanceMonitor;
	}

	public static DebugOverlay getDebugOverlay() {
		return debugOverlay;
	}

	public static MetricsCollector getMetricsCollector() {
		return metricsCollector;
	}

	public static ResourceManager getResourceManager() {
		return resourceManager;
	}
}
